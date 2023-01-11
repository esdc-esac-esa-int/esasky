package esac.archive.esasky.cl.web.client.repository;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.JavaScriptObject;
import esac.archive.absi.modules.cl.aladinlite.widget.client.model.AladinShape;
import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.callback.ICallback;
import esac.archive.esasky.cl.web.client.event.MultiSelectableDataInSkyChangedEvent;
import esac.archive.esasky.cl.web.client.model.SourceShapeType;
import esac.archive.esasky.cl.web.client.model.entities.*;
import esac.archive.esasky.cl.web.client.presenter.MainPresenter;
import esac.archive.esasky.cl.web.client.query.*;
import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
import esac.archive.esasky.cl.web.client.utility.CoordinateUtils;
import esac.archive.esasky.cl.web.client.utility.EsaSkyWebConstants;
import esac.archive.esasky.cl.web.client.utility.ProperMotionUtils;
import esac.archive.esasky.cl.web.client.view.ctrltoolbar.GwPanel;
import esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorSettings;
import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;
import esac.archive.esasky.ifcs.model.coordinatesutils.SkyViewPosition;
import esac.archive.esasky.ifcs.model.descriptor.CommonTapDescriptor;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class EntityRepository {

    private DescriptorRepository descriptorRepo;

    private PublicationsEntity publicationsEntity;
    private List<GeneralEntityInterface> allEntities = new LinkedList<>();
    private static EntityRepository _instance;

    public static EntityRepository init(DescriptorRepository descriptorRepo) {
        _instance = new EntityRepository(descriptorRepo);
        return _instance;
    }

    public static EntityRepository getInstance() {
        if (_instance == null) {
            throw new AssertionError("You have to call init first");
        }
        return _instance;
    }

    private EntityRepository(DescriptorRepository descriptorRepo) {
        this.descriptorRepo = descriptorRepo;
    }

    public GeneralEntityInterface getEntity(String id) {
        for (GeneralEntityInterface currEntity : allEntities) {
            if (currEntity.getId().equals(id)) {
                return currEntity;
            }
        }
        return null;
    }

    public void addEntity(GeneralEntityInterface newEntity) {
        if (newEntity != null) {
            allEntities.add(newEntity);
        }

        if(countEntitiesWithMultiSelectionEnabled() == 1 && !isPublicationEntityType(newEntity)) {
            CommonEventBus.getEventBus().fireEvent(new MultiSelectableDataInSkyChangedEvent(true));
        }
        CommonEventBus.getEventBus().fireEvent(new MultiSelectableDataInSkyChangedEvent(true));
    }

    public void removeEntity(GeneralEntityInterface entity) {
        allEntities.remove(entity);
        if(countEntitiesWithMultiSelectionEnabled() == 0) {
            CommonEventBus.getEventBus().fireEvent(new MultiSelectableDataInSkyChangedEvent(false));
        }
    }
    
    private int countEntitiesWithMultiSelectionEnabled() {
        int multiSelectionEntities = allEntities.size();

        for(GeneralEntityInterface entity : allEntities) {
            if(isPublicationEntityType(entity) 
            		|| Objects.equals(entity.getDescriptor().getSchemaName(), "alerts")
            		|| entity instanceof ImageListEntity
            		) {
                multiSelectionEntities--;
            }
        }
        return multiSelectionEntities;
    }
    
    public int checkNumberOfEntitesWithMultiSelection() {
        int multiSelectionEntities = countEntitiesWithMultiSelectionEnabled();
        if(multiSelectionEntities == 0) {
            CommonEventBus.getEventBus().fireEvent(new MultiSelectableDataInSkyChangedEvent(false));
        }
        else if(multiSelectionEntities == 1) {
            CommonEventBus.getEventBus().fireEvent(new MultiSelectableDataInSkyChangedEvent(true));
        }
        return multiSelectionEntities;
    }
    
    private boolean isPublicationEntityType(GeneralEntityInterface entity) {
        return entity instanceof PublicationsByAuthorEntity
                || entity instanceof PublicationsBySourceEntity
                || entity instanceof PublicationsEntity;
    }

    public List<String> getAllEntityNames() {
    	LinkedList<String> list = new LinkedList<String>();
    	for(GeneralEntityInterface ent : allEntities) {
    		list.add(ent.getId());
    	}
    	return list;
    }

    public List<GeneralEntityInterface> getAllEntities() {
    	return allEntities;
    }

    public ImageListEntity createImageListEntity(CommonTapDescriptor descriptor, ICallback footprintSelected) {
        ImageListEntity newEntity = new ImageListEntity(descriptor, descriptorRepo.getDescriptorCountAdapter(EsaSkyWebConstants.CATEGORY_IMAGES).getCountStatus(),
                CoordinateUtils.getCenterCoordinateInJ2000(), descriptor.getId(), TAPImageListService.getInstance(), selectedEntity -> {
            deselectOtherImageEntityShapes(selectedEntity);
            footprintSelected.onCallback();
        });
        addEntity(newEntity);
        return newEntity;
    }

  public void deselectOtherImageEntityShapes(ImageListEntity myEntity) {
      for (GeneralEntityInterface entity : allEntities) {
          if (entity != myEntity)  {
              entity.deselectAllShapes();
              entity.getTablePanel().deselectAllRows();
          }
      }
  }

    public GeneralEntityInterface createEntity(CommonTapDescriptor descriptor) {
        GeneralEntityInterface newEntity;

        switch(descriptor.getCategory()) {
            case EsaSkyWebConstants.CATEGORY_CATALOGUES:
                newEntity = createCatalogueEntity(descriptor);
                break;
            case EsaSkyWebConstants.CATEGORY_PUBLICATIONS:
                newEntity = createPublicationsEntity(descriptor);
                break;
            case EsaSkyWebConstants.CATEGORY_EXTERNAL:
                newEntity = createExternalTapEntity(descriptor);
                break;
            case EsaSkyWebConstants.CATEGORY_OBSERVATIONS:
                newEntity = createObservationEntity(descriptor);
                break;
            case EsaSkyWebConstants.CATEGORY_SSO:
                newEntity = new SSOEntity(descriptor);
                break;
            default:
                newEntity = new EsaSkyEntity(descriptor, descriptorRepo.getDescriptorCountAdapter(descriptor.getCategory()).getCountStatus(),
                        CoordinateUtils.getCenterCoordinateInJ2000(), descriptor.getId(), TAPObservationService.getInstance());
        }

        addEntity(newEntity);
        return newEntity;
    }
    
    public EsaSkyEntity createGwEntity(CommonTapDescriptor descriptor, String id, String lineStyle, String stcsColumn) {

        EsaSkyEntity gwEntity =  new EsaSkyEntity(descriptor, CoordinateUtils.getCenterCoordinateInJ2000(), id, lineStyle, TAPGwService.getInstance(), stcsColumn)
        {
            @Override
            public TabulatorSettings getTabulatorSettings() {
                TabulatorSettings settings = new TabulatorSettings();
                settings.setSelectable(1);
                settings.setDisableGoToColumn(true);
                settings.setAddLink2ArchiveColumn(false);
                settings.setAddSendToVOApplicationColumn(false);
                return settings;
            }

            @Override
            public void onShapeSelection(AladinShape shape) {
            	 MainPresenter.getInstance().getCtrlTBPresenter().openGWPanel(GwPanel.TabIndex.GW.ordinal());
            	 super.onShapeSelection(shape);
            }

            @Override
            public void onShapeDeselection(AladinShape shape) {
                onShapeSelection(shape);
            }

        };

        addEntity(gwEntity);
        return gwEntity;
    }

    public EsaSkyEntity createIceCubeEntity(CommonTapDescriptor descriptor) {
        EsaSkyEntity iceCubeEntity = new EsaSkyEntity(descriptor, CoordinateUtils.getCenterCoordinateInJ2000(), descriptor.getId(), "solid",  TAPIceCubeService.getInstance()) {
            @Override
            public void onShapeSelection(AladinShape shape) {
            	 MainPresenter.getInstance().getCtrlTBPresenter().openGWPanel(GwPanel.TabIndex.NEUTRINO.ordinal());
            	 super.onShapeSelection(shape);
            }
        };
        addEntity(iceCubeEntity);
        return iceCubeEntity;
    }

    public GeneralEntityInterface createExternalTapEntity(final CommonTapDescriptor descriptor) {
        return new ExtTapEntity(descriptor, descriptorRepo.getDescriptorCountAdapter(descriptor.getCategory()).getCountStatus(),
                CoordinateUtils.getCenterCoordinateInJ2000(), descriptor.getId(), TAPExtTapService.getInstance());
    }

    public GeneralEntityInterface createObservationEntity(final CommonTapDescriptor descriptor) {
        GeneralEntityInterface newEntity;

        if(descriptor.useIntersectsPolygon()) {
            newEntity = new EsaSkyEntity(descriptor, descriptorRepo.getDescriptorCountAdapter(descriptor.getCategory()).getCountStatus(),
                    CoordinateUtils.getCenterCoordinateInJ2000(), descriptor.getId(), TAPObservationService.getInstance());
        } else {
            newEntity = new EsaSkyEntity(descriptor, descriptorRepo.getDescriptorCountAdapter(descriptor.getCategory()).getCountStatus(),
                    CoordinateUtils.getCenterCoordinateInJ2000(), descriptor.getId(),
                    TAPObservationService.getInstance(), 20, SourceShapeType.CROSS.getName());
        }

        return newEntity;
    }

    // CATALOG -------------
    public GeneralEntityInterface createCatalogueEntity(final CommonTapDescriptor descriptor) {
        String esaSkyUniqId = descriptor.getId();
        SkyViewPosition skyViewPosition = CoordinateUtils.getCenterCoordinateInJ2000();

        return new EsaSkyEntity(descriptor, descriptorRepo.getDescriptorCountAdapter(EsaSkyWebConstants.CATEGORY_CATALOGUES).getCountStatus(),
                skyViewPosition, esaSkyUniqId, TAPObservationService.getInstance(), new EsaSkyEntity.SecondaryShapeAdder() {

                    @Override
                    public void createSpecializedOverlayShape(Map<String, Object> details) {
                        if (descriptor.hasProperMotion()) {
                            JavaScriptObject functionPointer = AladinLiteWrapper.getAladinLite()
                                    .createFunctionPointer("drawSourceWithProperMotion");
                            details.put("shape", functionPointer);

                            // Adds the arrow style details
                            details.put("arrowColor", "#33ccff");
                            details.put("arrowWidth", 3.0);
                            details.put("arrowLength", 3.0 * 6);
                        }
                    }

                    @Override
                    public void addSecondaryShape(GeneralJavaScriptObject rowData, String ra, String dec, Map<String, String> details) {
                        if (descriptor.hasProperMotion()) {

                            // Adds the details for drawing the proper motion arrows
                            try {

                                // Proper motion ra, dec not coming from descriptor data, so we need to
                                // calculate it
                                final Double pm_ra = rowData
                                        .getDoubleOrNullProperty(descriptor.getProperMotionRaColumn());
                                final Double pm_dec = rowData
                                        .getDoubleOrNullProperty(descriptor.getProperMotionDecColumn());

                                final boolean hasValidData = pm_ra != null
                                        && pm_dec != null
                                        && descriptor.getReferenceEpochColumn() != null;

                                if (hasValidData) {
                                    double[] inputA = new double[6];
                                    inputA[0] = Double.parseDouble(ra);
                                    inputA[1] = Double.parseDouble(dec);
                                    inputA[2] = rowData.getDoubleProperty(descriptor.getParallaxTrigColumn()); // Consider
                                                                                                           // the
                                                                                                           // parallax
                                                                                                           // as
                                                                                                           // 0.0
                                                                                                           // by
                                                                                                           // default
                                    inputA[3] = pm_ra;
                                    inputA[4] = pm_dec;
                                    inputA[5] = rowData
                                            .getDoubleProperty(descriptor.getRadialVelocityColumn()); // normalised
                                                                                                       // radial
                                                                                                       // velocity
                                                                                                       // at t0
                                                                                                       // [mas/yr]
                                                                                                       // - see
                                                                                                       // Note 2

                                    double[] outputA = new double[6];

                                    ProperMotionUtils.pos_prop(rowData.getDoubleProperty(descriptor.getReferenceEpochColumn()), inputA, 2000, outputA);

                                    double finalRa = outputA[0];
                                    double finalDec = outputA[1];

                                    if (rowData.getDoubleProperty(descriptor.getReferenceEpochColumn()) > 2000.0) {
                                        // For catalogs in J2015 put the source in J2015 but draw the arrow
                                        // flipped... from J2000 to J2015
                                        details.put("arrowFlipped", "true");
                                    }

                                    details.put("arrowRa", finalRa + "");
                                    details.put("arrowDec", finalDec + "");

                                    // Creates the ra dec normalized vector of 10 degrees
                                    final double raInc = finalRa - Double.parseDouble(ra);
                                    final double decInc = finalDec - Double.parseDouble(dec);
                                    final double m = Math.sqrt((raInc * raInc) + (decInc * decInc));
                                    final double mRatio = 2 / m; // 2 degrees
                                    final double raNorm = Double.parseDouble(ra) + (raInc * mRatio);
                                    final double decNorm = Double.parseDouble(dec) + (decInc * mRatio);

                                    // Calculates the scale factor, this function is also in AladinESDC.js (modify
                                    // there also)
                                    final double arrowRatio = 4.0
                                            - (3.0 * Math.pow(Math.log((m * 1000) + 2.72), -2.0)); // See:
                                    // //rechneronline.de/function-graphs/
                                    // , using
                                    // function: 4 -
                                    // (3*(log((x*
                                    // 1000)+2.72)^(-2)))

                                    details.put("arrowRaNorm", raNorm + "");
                                    details.put("arrowDecNorm", decNorm + "");
                                    details.put("arrowRatio", arrowRatio + "");
                                }
                            } catch (Exception ex) {
                                Log.error(this.getClass().getSimpleName() + ", Calculate proper motion error: ", ex);
                            }
                        }
                    }
                });
    }

    // PUBLICATIONS -------------
    public PublicationsEntity getPublications() {
        return publicationsEntity;
    }

    public PublicationsEntity createPublicationsEntity(CommonTapDescriptor descriptor) {
        String esaSkyUniqID = descriptor.getId();
        SkyViewPosition skyViewPosition = CoordinateUtils.getCenterCoordinateInJ2000();

        publicationsEntity = new PublicationsEntity(descriptor,
                descriptorRepo.getDescriptorCountAdapter(EsaSkyWebConstants.CATEGORY_PUBLICATIONS).getCountStatus(), skyViewPosition,
                esaSkyUniqID);

        addEntity(publicationsEntity);

        return publicationsEntity;
    }

    // PUBLICATIONS BY SOURCE -------------

    public GeneralEntityInterface createPublicationsBySourceEntity(String sourceName, double ra, double dec, String bibcount) {
        final CommonTapDescriptor descriptor = descriptorRepo.getFirstDescriptor(EsaSkyWebConstants.CATEGORY_PUBLICATIONS);
        SkyViewPosition skyViewPosition = CoordinateUtils.getCenterCoordinateInJ2000();

        PublicationsBySourceEntity pubBySourceEntity = new PublicationsBySourceEntity(descriptor,
                descriptorRepo.getDescriptorCountAdapter(EsaSkyWebConstants.CATEGORY_PUBLICATIONS).getCountStatus(), skyViewPosition,
                sourceName, ra, dec, bibcount);
        addEntity(pubBySourceEntity);

        return pubBySourceEntity;
    }

    
    public GeneralEntityInterface createPublicationsByAuthorEntity(String author) {
        final CommonTapDescriptor descriptor = descriptorRepo.getFirstDescriptor(EsaSkyWebConstants.CATEGORY_PUBLICATIONS);
        SkyViewPosition skyViewPosition = CoordinateUtils.getCenterCoordinateInJ2000();

        GeneralEntityInterface pubByAuthorEntity = new PublicationsByAuthorEntity(descriptor,
                descriptorRepo.getDescriptorCountAdapter(EsaSkyWebConstants.CATEGORY_PUBLICATIONS).getCountStatus(), skyViewPosition,
                author);

        addEntity(pubByAuthorEntity);

        return pubByAuthorEntity;
    }
}