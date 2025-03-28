/*
ESASky
Copyright (C) 2025 European Space Agency

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published
by the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

package esac.archive.esasky.cl.web.client.model;

import com.allen_sauer.gwt.log.client.Log;
import com.github.nmorel.gwtjackson.client.ObjectMapper;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.Timer;
import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.event.OpenSeaDragonActiveEvent;
import esac.archive.esasky.cl.web.client.event.TargetDescriptionEvent;
import esac.archive.esasky.cl.web.client.event.hips.HipsAddedEvent;
import esac.archive.esasky.cl.web.client.query.TAPImageListService;
import esac.archive.esasky.cl.web.client.utility.*;
import esac.archive.esasky.cl.web.client.utility.JSONUtils.IJSONRequestCallback;
import esac.archive.esasky.cl.web.client.utility.OpenSeaDragonWrapper.OpenSeaDragonType;
import esac.archive.esasky.cl.web.client.view.ctrltoolbar.selectsky.SelectSkyPanel;
import esac.archive.esasky.cl.web.client.view.ctrltoolbar.selectsky.SkyRow;
import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;
import esac.archive.esasky.ifcs.model.client.HiPS;
import esac.archive.esasky.ifcs.model.client.HipsWavelength;
import esac.archive.esasky.ifcs.model.coordinatesutils.Coordinate;
import esac.archive.esasky.ifcs.model.coordinatesutils.SkyViewPosition;
import esac.archive.esasky.ifcs.model.descriptor.CommonTapDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.OutreachImageDescriptor;
import esac.archive.esasky.ifcs.model.shared.ColorPalette;
import esac.archive.esasky.ifcs.model.shared.EsaSkyConstants;

import java.util.Objects;

public class OutreachImage {

    private String id;
    private String title;
    private String description;
    private String credits;
    private String baseUrl;
    private double opacity = 1.0;
    private boolean removed = false;
    private boolean isHips = false;
    private String hipsSurveyName = null;
    private OpenSeaDragonWrapper lastOpenseadragon = null;
    private final String mission;

    public OutreachImage(String id, double opacity, CommonTapDescriptor descriptor) {
        this.id = id;
        this.opacity = opacity;
        this.mission = descriptor.getMission();
    }

    public OutreachImage(GeneralJavaScriptObject imageObject, double opacity, String mission) {
        this(imageObject, opacity, mission, true, false);
    }


    public OutreachImage(GeneralJavaScriptObject imageObject, double opacity, String mission, boolean moveToCenter, boolean hideDescription) {
        this.opacity = opacity;

        OutreachImageDescriptorMapper mapper = GWT.create(OutreachImageDescriptorMapper.class);
        String newJson = imageObject.jsonStringify().replaceAll("\"(\\[\\d+?,\\s?\\d+?\\])\"", "$1");
        OutreachImageDescriptor desc = mapper.read(newJson);
        onResponseParsed(desc, mission, moveToCenter, hideDescription);
        this.mission = mission;
    }

    public interface OutreachImageDescriptorMapper extends ObjectMapper<OutreachImageDescriptor> {}

    public void loadImage(CommonTapDescriptor descriptor, TAPImageListService metadataService) {
        loadImage(descriptor, metadataService, true);
    }

    public void loadImage(CommonTapDescriptor descriptor, TAPImageListService metadataService, boolean moveToCenter) {
        String mission = this.mission;
        String query = descriptor.createTapUrl(metadataService.getRequestUrl(descriptor), metadataService.getImageMetadata(descriptor, this.id), EsaSkyConstants.JSON);
        JSONUtils.getJSONFromUrl(query, new IJSONRequestCallback() {

            @Override
            public void onSuccess(String responseText) {

                GeneralJavaScriptObject rawObject = GeneralJavaScriptObject.createJsonObject(responseText);
                GeneralJavaScriptObject[] metadata = GeneralJavaScriptObject.convertToArray(rawObject.getProperty("metadata"));
                GeneralJavaScriptObject[] data = GeneralJavaScriptObject.convertToArray(GeneralJavaScriptObject.convertToArray(rawObject.getProperty("data"))[0]);

                GeneralJavaScriptObject newObj = GeneralJavaScriptObject.createJsonObject("{}");
                for (int i = 0; i < metadata.length; i++) {
                    String metaName = metadata[i].getStringProperty("name");
                    newObj.setProperty(metaName, data[i].toString());
                }

                OutreachImageDescriptorMapper mapper = GWT.create(OutreachImageDescriptorMapper.class);
                String newJson = newObj.jsonStringify().replace("\"[", "[").replace("]\"", "]");
                OutreachImageDescriptor desc = mapper.read(newJson);

                onResponseParsed(desc, mission, moveToCenter, false);
            }

            @Override
            public void onError(String errorCause) {
                Log.error(errorCause);
            }
        });
    }

    public void removeOpenSeaDragon() {
        removed = true;
        CommonEventBus.getEventBus().fireEvent(new OpenSeaDragonActiveEvent(false));
        if (lastOpenseadragon != null) {
            lastOpenseadragon.removeOpenSeaDragonFromAladin();
        }
    }

    public void reattachOpenSeaDragon() {
        removed = false;
        CommonEventBus.getEventBus().fireEvent(new OpenSeaDragonActiveEvent(true));
//		loadImage();
    }

    public boolean isRemoved() {
        return removed;
    }

    public String getId() {
        return id;
    }

    public void onResponseParsed(OutreachImageDescriptor desc, String mission, boolean moveToCenter, boolean hideDescription) {
        String url = desc.getTilesUrl();
        this.id = desc.getId();

        boolean isHst = Objects.equals(mission, EsaSkyConstants.HST_MISSION);
        this.baseUrl = isHst
                ? "https://esahubble.org/images/" + id
                : "https://esawebb.org/images/" + id;

        boolean isOrion = desc.getId().equalsIgnoreCase("webb_orionnebula_longwave") || desc.getId().equalsIgnoreCase("webb_orionnebula_shortwave");
        if (isOrion) {
            this.baseUrl = "https://www.esa.int/Science_Exploration/Space_Science/Webb/Webb_s_wide-angle_view_of_the_Orion_Nebula_is_released_in_ESASky";
        }

        this.title = desc.getTitle();
        this.description = desc.getDescription();
        this.credits = desc.getCredit();
        this.isHips = url.startsWith("hips:");
        Coordinate coor = new Coordinate(desc.getRa(), desc.getDec());

        if (this.isHips) {
            final String hipsUrl = url.split("hips:")[1];
            HipsParser parser = new HipsParser(new HipsParserObserver() {
                @Override
                public void onSuccess(HiPS hips) {
                    hipsSurveyName = hips.getSurveyName();
                    hips.setColorPalette(ColorPalette.NATIVE);
                    CommonEventBus.getEventBus().fireEvent(new HipsAddedEvent(hips, HipsWavelength.OUTREACH, false));
					GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_OUTREACHIMAGES, GoogleAnalytics.ACT_GW_SHOW_HIPS, hipsUrl);
                }

                @Override
                public void onError(String errorMsg) {

                }
            });
            parser.loadProperties(hipsUrl);
        } else {
            ImageSize imageSize = new ImageSize(desc.getPixelSize()[0], desc.getPixelSize()[1]);

            OpenSeaDragonType type = OpenSeaDragonType.TILED;
            if (url == null || imageSize.getHeight() < 1000 || imageSize.getWidth() < 1000) {
                url = desc.getLargeUrl();
                type = OpenSeaDragonType.SINGLE;
            }
            OpenSeaDragonWrapper openseadragonWrapper = new OpenSeaDragonWrapper(this.id, url, type,
                    coor.getRa(), coor.getDec(), desc.getFovSize(), desc.getRotation(), imageSize.getWidth(), imageSize.getHeight());


            openseadragonWrapper.addTileLoadedEventHandler(event -> {
                String action;
                if (isHst) {
                    action = event.isSuccess()
                            ? GoogleAnalytics.ACT_IMAGES_HSTIMAGE_SUCCESS
                            : GoogleAnalytics.ACT_IMAGES_HSTIMAGE_FAIL;
                } else if (Objects.equals(mission, EsaSkyConstants.JWST_MISSION)) {
                    action = event.isSuccess()
                            ? GoogleAnalytics.ACT_IMAGES_JWSTIMAGE_SUCCESS
                            : GoogleAnalytics.ACT_IMAGES_JWSTIMAGE_FAIL;
                } else {
                    action = event.isSuccess()
                            ? GoogleAnalytics.ACT_IMAGES_EUCLIDIMAGE_SUCCESS
                            : GoogleAnalytics.ACT_IMAGES_EUCLIDIMAGE_FAIL;
                }

                GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_IMAGES, action, desc.getId());
            });
            lastOpenseadragon = openseadragonWrapper;
            JavaScriptObject openSeaDragonObject = openseadragonWrapper.createOpenSeaDragonObject();
            openseadragonWrapper.addOpenSeaDragonToAladin(openSeaDragonObject);
        }


        if (moveToCenter) {
            moveToCenter(coor, desc);
        }

        Timer timer = new Timer() {

            @Override
            public void run() {
                if (isHips) {
                    for (SkyRow sr : SelectSkyPanel.getInstance().getHipsList()) {
                        if (Objects.equals(sr.getSelectedHips().getHipsWavelength(), HipsWavelength.OUTREACH)) {
                            sr.setOpacity(opacity);
                        }
                    }
                } else {
                    AladinLiteWrapper.getAladinLite().setOpenSeaDragonOpacity(opacity);
                    AladinLiteWrapper.getAladinLite().requestRedraw();
                }
            }
        };
        timer.schedule(100);
        AladinLiteWrapper.getAladinLite().setOpenSeaDragonOpacity(opacity);

        if (!hideDescription) {
            StringBuilder popupText = new StringBuilder(this.description);
            popupText.append("<br>  Credit: ");
            popupText.append(this.credits);

            if (!Objects.equals(mission, EsaSkyWebConstants.EUCLID_MISSION)) {
                popupText.append("<br><br> This image on <a target=\"_blank\" href=\" " + this.baseUrl + (isHst ? "\">ESA Hubble News</a>" : "\">ESA Webb News</a>"));
            }

            CommonEventBus.getEventBus().fireEvent(
                    new TargetDescriptionEvent(this.title, popupText.toString(), false));
        }

        CommonEventBus.getEventBus().fireEvent(new OpenSeaDragonActiveEvent(true));
        if (removed) {
            removeOpenSeaDragon();
        }

    }

    private void moveToCenter(Coordinate coor, OutreachImageDescriptor desc) {
        SkyViewPosition curPos = CoordinateUtils.getCenterCoordinateInJ2000();
        try {
            double dist = curPos.getCoordinate().distance(coor);

            if (curPos.getFov() / desc.getFovSize() > 5 || desc.getFovSize() / curPos.getFov() < .2 || dist > curPos.getFov() / 2) {
                AladinLiteWrapper.getAladinLite().goToRaDec(Double.toString(coor.getRa()), Double.toString(coor.getDec()));
                AladinLiteWrapper.getAladinLite().setZoom(desc.getFovSize() * 3);
            }
        } catch (NullPointerException e) {
            // Might happen if it is loaded before AladinLite is ready. THen make sure to go to position
            Log.error(e.getMessage(), e);
            AladinLiteWrapper.getAladinLite().goToRaDec(Double.toString(coor.getRa()), Double.toString(coor.getDec()));
            AladinLiteWrapper.getAladinLite().setZoom(desc.getFovSize() * 3);

        }
    }

    public double getOpacity() {
        return opacity;
    }

    public void setOpacity(double opacity) {
        if (removed) {
            return;
        }
        this.opacity = opacity;
        if (isHips) {
            for (SkyRow sr : SelectSkyPanel.getInstance().getHipsList()) {
                if (Objects.equals(sr.getSelectedHips().getHipsWavelength(), HipsWavelength.OUTREACH)) {
                    sr.setOpacity(opacity);
                }
            }
        } else {
            AladinLiteWrapper.getAladinLite().setOpenSeaDragonOpacity(opacity);
        }
    }

    public boolean isHips() {
        return this.isHips;
    }

    public String getSurveyName() {
        return this.hipsSurveyName;
    }
}
