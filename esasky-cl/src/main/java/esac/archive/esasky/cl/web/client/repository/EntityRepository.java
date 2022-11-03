package esac.archive.esasky.cl.web.client.repository;

import esac.archive.absi.modules.cl.aladinlite.widget.client.model.AladinShape;
import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.callback.ICallback;
import esac.archive.esasky.cl.web.client.event.MultiSelectableDataInSkyChangedEvent;
import esac.archive.esasky.cl.web.client.model.entities.*;
import esac.archive.esasky.cl.web.client.presenter.MainPresenter;
import esac.archive.esasky.cl.web.client.query.*;
import esac.archive.esasky.cl.web.client.status.CountStatus;
import esac.archive.esasky.cl.web.client.utility.CoordinateUtils;
import esac.archive.esasky.cl.web.client.utility.EsaSkyWebConstants;
import esac.archive.esasky.cl.web.client.view.ctrltoolbar.GwPanel;
import esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorSettings;
import esac.archive.esasky.ifcs.model.descriptor.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class EntityRepository {

    private DescriptorRepository descriptorRepo;

//    private PublicationsEntity publicationsEntity;
    private List<GeneralEntityInterface> allEntities = new LinkedList<GeneralEntityInterface>();
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
            if (currEntity.getEsaSkyUniqId().equals(id)) {
                return currEntity;
            }
        }
        return null;
    }

    public void addEntity(GeneralEntityInterface newEntity) {
        if (newEntity != null) {
            allEntities.add(newEntity);
        }
        // TODO: fix
//        if(countEntitiesWithMultiSelectionEnabled() == 1 && !isPublicationEntityType(newEntity)) {
//            CommonEventBus.getEventBus().fireEvent(new MultiSelectableDataInSkyChangedEvent(true));
//        }
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
//            		|| entity instanceof ImageListEntity // TODO: FIX
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
//        return entity instanceof PublicationsByAuthorEntity
//                || entity instanceof PublicationsBySourceEntity
//                || entity instanceof PublicationsEntity;
        return false; // TODO:fix
    }

    public List<String> getAllEntityNames() {
    	LinkedList<String> list = new LinkedList<String>();
    	for(GeneralEntityInterface ent : allEntities) {
    		list.add(ent.getEsaSkyUniqId());
    	}
    	return list;
    }

    public List<GeneralEntityInterface> getAllEntities() {
    	return allEntities;
    }

    public ImageListEntity createImageListEntity(BaseDescriptor descriptor, ICallback footprintSelected) {
        // TODO: FIX
        return null;
//    	ImageListEntity newEntity =  new ImageListEntity(descriptor, descriptorRepo.getImageDescriptors().getCountStatus(),
//                CoordinateUtils.getCenterCoordinateInJ2000(), descriptor.getDescriptorId(), TAPImageListService.getInstance(), selectedEntity -> {
////                    deselectOtherImageEntityShapes(selectedEntity); // TODO: FIX
//                    footprintSelected.onCallback();
//                });
//    	addEntity(newEntity);
//    	return newEntity;
    }
//
//    public void deselectOtherImageEntityShapes(ImageListEntity myEntity) {
//        for (GeneralEntityInterface entity : allEntities) {
//            if (entity != myEntity)  {
//                entity.deselectAllShapes();
//                entity.getTablePanel().deselectAllRows();
//            }
//        }
//    }

    public GeneralEntityInterface createEntity(CommonTapDescriptor descriptor) {
        GeneralEntityInterface newEntity = createEntity(descriptor,
                descriptorRepo.getDescriptorCountAdapter(descriptor.getCategory()).getCountStatus(),
                TAPObservationService.getInstance());

        addEntity(newEntity);
        return newEntity;

        // TODO: Fix
//        if (descriptor instanceof SSODescriptor) {
//            newEntity = new SSOEntity(descriptor);
//            addEntity(newEntity);
//        } else if (descriptor instanceof TapDescriptor) {
//            if(descriptor.getUseIntersectPolygonInsteadOfContainsPoint()) {
//                return createEntity(descriptor, descriptorRepo.getObsDescriptors().getCountStatus(),
//                        TAPObservationService.getInstance());
//            } else {
//                newEntity = new EsaSkyEntity(descriptor, descriptorRepo.getObsDescriptors().getCountStatus(),
//                        CoordinateUtils.getCenterCoordinateInJ2000(), descriptor.generateId(),
//                        TAPObservationService.getInstance(), 20, SourceShapeType.CROSS.getName());
//                addEntity(newEntity);
//            }
//        } else if (descriptor instanceof SpectraDescriptor) {
//        	return createEntity(descriptor, descriptorRepo.getSpectraDescriptors().getCountStatus(),
//        			TAPObservationService.getInstance());
//        } else if (descriptor instanceof CatalogDescriptor) {
//            return createCatalogueEntity((CatalogDescriptor) descriptor);
//        } else if (descriptor instanceof ExtTapDescriptor) {
//        	newEntity =  new ExtTapEntity(descriptor, descriptorRepo.getExtTapDescriptors().getCountStatus(),
//                    CoordinateUtils.getCenterCoordinateInJ2000(), descriptor.generateId(), TAPExtTapService.getInstance());
//            addEntity(newEntity);
//	    }

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
            	MainPresenter.getInstance().getCtrlTBPresenter().openGWPanel(GwPanel.TabIndex.GW.ordinal());
            	super.onShapeSelection(shape);
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

    private GeneralEntityInterface createEntity(CommonTapDescriptor descriptor, CountStatus countStatus,
                                                AbstractTAPService metadataService) {
        GeneralEntityInterface newEntity = new EsaSkyEntity(descriptor, countStatus,
                CoordinateUtils.getCenterCoordinateInJ2000(), descriptor.getId(), metadataService);

        addEntity(newEntity);
        return newEntity;
    }

    // TODO: fix this
    // CATALOG -------------
//    public GeneralEntityInterface createCatalogueEntity(final CatalogDescriptor descriptor) {
//        String esaSkyUniqId = descriptor.generateId();
//        SkyViewPosition skyViewPosition = CoordinateUtils.getCenterCoordinateInJ2000();
//        GeneralEntityInterface newCatEntity = new EsaSkyEntity(descriptor, descriptorRepo.getCatDescriptors().getCountStatus(),
//                skyViewPosition, esaSkyUniqId, TAPCatalogueService.getInstance(), new SecondaryShapeAdder() {
//
//                    @Override
//                    public void createSpecializedOverlayShape(Map<String, Object> details) {
//                        if (descriptor.getDrawSourcesFunction() != null
//                                && !descriptor.getDrawSourcesFunction().isEmpty()) {
//                            JavaScriptObject functionPointer = AladinLiteWrapper.getAladinLite()
//                                    .createFunctionPointer(descriptor.getDrawSourcesFunction());
//                            details.put("shape", functionPointer);
//
//                            // Adds the arrow style details
//                            details.put("arrowColor", descriptor.getSecondaryColor());
//                            details.put("arrowWidth", descriptor.getPmArrowWidth());
//                            details.put("arrowLength", descriptor.getPmArrowWidth() * 6);
//                        }
//                    }
//
//                    @Override
//                    public void addSecondaryShape(GeneralJavaScriptObject rowData, String ra, String dec,
//                            Map<String, String> details) {
//                        if (descriptor.getDrawSourcesFunction() != null) {
//
//                            // Adds the details for drawing the proper motion arrows
//                            try {
//
//                                Double finalRa = null;
//                                Double finalDec = null;
//
//                                if ((descriptor.getFinalRaTapColumn() != null)
//                                        && descriptor.getFinalDecTapColumn() != null) {
//
//                                    // Proper motion ra, dec is coming from descriptor data, just use it
//                                    finalRa = rowData.getDoubleOrNullProperty(descriptor.getFinalRaTapColumn());
//                                    finalDec = rowData.getDoubleOrNullProperty(descriptor.getFinalDecTapColumn());
//
//                                } else {
//
//                                    // Proper motion ra, dec not coming from descriptor data, so we need to
//                                    // calculate it
//
//                                    final Double pm_ra = rowData
//                                            .getDoubleOrNullProperty(descriptor.getPmRaTapColumn());
//                                    final Double pm_dec = rowData
//                                            .getDoubleOrNullProperty(descriptor.getPmDecTapColumn());
//
//                                    if ((pm_ra != null) && (pm_dec != null) && (descriptor.getPmOrigEpoch() != null)
//                                            && (descriptor.getPmFinalEpoch() != null)) {
//
//                                        double[] inputA = new double[6];
//                                        inputA[0] = new Double(ra);
//                                        inputA[1] = new Double(dec);
//                                        inputA[2] = rowData.getDoubleProperty(descriptor.getPmPlxTapColumn()); // Consider
//                                                                                                               // the
//                                                                                                               // parallax
//                                                                                                               // as
//                                                                                                               // 0.0
//                                                                                                               // by
//                                                                                                               // default
//                                        inputA[3] = pm_ra;
//                                        inputA[4] = pm_dec;
//                                        inputA[5] = rowData
//                                                .getDoubleProperty(descriptor.getPmNormRadVelTapColumn()); // normalised
//                                                                                                           // radial
//                                                                                                           // velocity
//                                                                                                           // at t0
//                                                                                                           // [mas/yr]
//                                                                                                           // - see
//                                                                                                           // Note 2
//
//                                        double[] outputA = new double[6];
//
//                                        ProperMotionUtils.pos_prop(descriptor.getPmOrigEpoch(), inputA,
//                                                descriptor.getPmFinalEpoch(), outputA);
//
//                                        finalRa = outputA[0];
//                                        finalDec = outputA[1];
//
//                                        if (descriptor.getPmOrigEpoch() > descriptor.getPmFinalEpoch()) {
//                                            // For catalogs in J2015 put the source in J2015 but draw the arrow
//                                            // flipped... from J2000 to J2015
//                                            details.put("arrowFlipped", "true");
//                                        }
//                                    }
//                                }
//
//                                if ((finalRa != null) && (finalDec != null)) {
//                                    details.put("arrowRa", finalRa + "");
//                                    details.put("arrowDec", finalDec + "");
//
//                                    // Creates the ra dec normalized vector of 10 degrees
//                                    final double raInc = finalRa - new Double(ra);
//                                    final double decInc = finalDec - new Double(dec);
//                                    final double m = Math.sqrt((raInc * raInc) + (decInc * decInc));
//                                    final double mRatio = 2 / m; // 2 degrees
//                                    final double raNorm = new Double(ra) + (raInc * mRatio);
//                                    final double decNorm = new Double(dec) + (decInc * mRatio);
//
//                                    // Calculates the scale factor, this function is also in AladinESDC.js (modify
//                                    // there also)
//                                    final double arrowRatio = 4.0
//                                            - (3.0 * Math.pow(Math.log((m * 1000) + 2.72), -2.0)); // See:
//                                                                                                   // //rechneronline.de/function-graphs/
//                                                                                                   // , using
//                                                                                                   // function: 4 -
//                                                                                                   // (3*(log((x*
//                                                                                                   // 1000)+2.72)^(-2)))
//
//                                    details.put("arrowRaNorm", raNorm + "");
//                                    details.put("arrowDecNorm", decNorm + "");
//                                    details.put("arrowRatio", arrowRatio + "");
//                                }
//
//                            } catch (Exception ex) {
//                                Log.error(this.getClass().getSimpleName() + ", Calculate proper motion error: ",
//                                        ex);
//                            }
//                        }
//                    }
//                });
//
//        addEntity(newCatEntity);
//
//        return newCatEntity;
//    }

    // PUBLICATIONS -------------
// TODO: FIX
//    public PublicationsEntity getPublications() {
//        return publicationsEntity;
//    }
// TODO: FIX
//    public PublicationsEntity createPublicationsEntity(PublicationsDescriptor descriptor) {
//
//        String esaSkyUniqID = descriptor.generateId();
//        SkyViewPosition skyViewPosition = CoordinateUtils.getCenterCoordinateInJ2000();
//
//        publicationsEntity = new PublicationsEntity(descriptor,
//                descriptorRepo.getPublicationsDescriptors().getCountStatus(), skyViewPosition,
//                esaSkyUniqID);
//        allEntities.add(publicationsEntity);
//
//        return publicationsEntity;
//    }

    // PUBLICATIONS BY SOURCE -------------

    public GeneralEntityInterface createPublicationsBySourceEntity(String sourceName, double ra, double dec, String bibcount) {
        // TODO: FIX
        return null;
//        final PublicationsDescriptor descriptor = descriptorRepo.getPublicationsDescriptors().getDescriptors().get(0);
//        SkyViewPosition skyViewPosition = CoordinateUtils.getCenterCoordinateInJ2000();
//
//        PublicationsBySourceEntity pubBySourceEntity = new PublicationsBySourceEntity(descriptor,
//                descriptorRepo.getPublicationsDescriptors().getCountStatus(), skyViewPosition,
//                sourceName, ra, dec, bibcount);
//        allEntities.add(pubBySourceEntity);
//
//        return pubBySourceEntity;
    }

    
    public GeneralEntityInterface createPublicationsByAuthorEntity(String author) {
        // TODO: FIX
//        final PublicationsDescriptor descriptor = descriptorRepo.getPublicationsDescriptors().getDescriptors().get(0);
//        SkyViewPosition skyViewPosition = CoordinateUtils.getCenterCoordinateInJ2000();
//
//        GeneralEntityInterface pubByAuthorEntity = new PublicationsByAuthorEntity(descriptor,
//                descriptorRepo.getPublicationsDescriptors().getCountStatus(), skyViewPosition,
//                author);
//
//        allEntities.add(pubByAuthorEntity);
//
//        return pubByAuthorEntity;
        return null;
    }
}