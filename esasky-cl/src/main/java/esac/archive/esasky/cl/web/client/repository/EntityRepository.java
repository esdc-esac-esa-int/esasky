package esac.archive.esasky.cl.web.client.repository;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.JavaScriptObject;
import esac.archive.esasky.ifcs.model.coordinatesutils.SkyViewPosition;
import esac.archive.esasky.ifcs.model.descriptor.CatalogDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.ExtTapDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.IDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.ObservationDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.PublicationsDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.SSODescriptor;
import esac.archive.esasky.ifcs.model.descriptor.SpectraDescriptor;
import esac.archive.esasky.cl.web.client.model.entities.EsaSkyEntity;
import esac.archive.esasky.cl.web.client.model.entities.EsaSkyEntity.SecondaryShapeAdder;
import esac.archive.esasky.cl.web.client.model.entities.ExtTapEntity;
import esac.archive.esasky.cl.web.client.model.entities.GeneralEntityInterface;
import esac.archive.esasky.cl.web.client.model.entities.PublicationsByAuthorEntity;
import esac.archive.esasky.cl.web.client.model.entities.PublicationsBySourceEntity;
import esac.archive.esasky.cl.web.client.model.entities.PublicationsEntity;
import esac.archive.esasky.cl.web.client.model.entities.SSOEntity;
import esac.archive.esasky.cl.web.client.query.AbstractTAPService;
import esac.archive.esasky.cl.web.client.query.TAPExtTapService;
import esac.archive.esasky.cl.web.client.query.TAPCatalogueService;
import esac.archive.esasky.cl.web.client.query.TAPObservationService;
import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.event.MultiSelectableDataInSkyChangedEvent;
import esac.archive.esasky.cl.web.client.model.SourceShapeType;
import esac.archive.esasky.cl.web.client.status.CountStatus;
import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
import esac.archive.esasky.cl.web.client.utility.CoordinateUtils;
import esac.archive.esasky.cl.web.client.utility.ProperMotionUtils;
import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;

public class EntityRepository {

    private DescriptorRepository descriptorRepo;

    private PublicationsEntity publicationsEntity;
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
        if(countEntitiesWithMultiSelectionEnabled() == 1 && !isPublicationEntityType(newEntity)) {
            CommonEventBus.getEventBus().fireEvent(new MultiSelectableDataInSkyChangedEvent(true));
        }
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
            if(isPublicationEntityType(entity)) {
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
    		list.add(ent.getEsaSkyUniqId());
    	}
    	return list;
    }

    public List<GeneralEntityInterface> getAllEntities() {
    	return allEntities;
    }

    public GeneralEntityInterface createEntity(IDescriptor descriptor) {
        GeneralEntityInterface newEntity = null;
        if (descriptor instanceof SSODescriptor) {
            newEntity = new SSOEntity(descriptor);
            addEntity(newEntity);
        } else if (descriptor instanceof ObservationDescriptor) {
            if(descriptor.getUseIntersectPolygonInsteadOfContainsPoint()) {
                return createEntity(descriptor, descriptorRepo.getObsDescriptors().getCountStatus(),
                        TAPObservationService.getInstance());
            } else {
                newEntity = new EsaSkyEntity(descriptor, descriptorRepo.getObsDescriptors().getCountStatus(),
                        CoordinateUtils.getCenterCoordinateInJ2000(), descriptor.generateId(),
                        TAPObservationService.getInstance(), 20, SourceShapeType.CROSS.getName());
                addEntity(newEntity);
            }
        } else if (descriptor instanceof SpectraDescriptor) {
        	return createEntity(descriptor, descriptorRepo.getSpectraDescriptors().getCountStatus(),
        			TAPObservationService.getInstance());
        } else if (descriptor instanceof CatalogDescriptor) {
            return createCatalogueEntity((CatalogDescriptor) descriptor);
        } else if (descriptor instanceof ExtTapDescriptor) {
        	newEntity =  new ExtTapEntity(descriptor, descriptorRepo.getExtTapDescriptors().getCountStatus(),
                    CoordinateUtils.getCenterCoordinateInJ2000(), descriptor.generateId(), TAPExtTapService.getInstance());
            addEntity(newEntity);
        }
        return newEntity;
    }

    private GeneralEntityInterface createEntity(IDescriptor descriptor, CountStatus countStatus,
            AbstractTAPService metadataService) {
        GeneralEntityInterface newEntity = new EsaSkyEntity(descriptor, countStatus,
                CoordinateUtils.getCenterCoordinateInJ2000(), descriptor.generateId(), metadataService);
        addEntity(newEntity);
        return newEntity;
    }

    // CATALOG -------------
    public GeneralEntityInterface createCatalogueEntity(final CatalogDescriptor descriptor) {
        String esaSkyUniqId = descriptor.generateId();
        SkyViewPosition skyViewPosition = CoordinateUtils.getCenterCoordinateInJ2000();
        GeneralEntityInterface newCatEntity = new EsaSkyEntity(descriptor, descriptorRepo.getCatDescriptors().getCountStatus(),
                skyViewPosition, esaSkyUniqId, TAPCatalogueService.getInstance(), new SecondaryShapeAdder() {

                    @Override
                    public void createSpecializedOverlayShape(Map<String, Object> details) {
                        if (descriptor.getDrawSourcesFunction() != null
                                && !descriptor.getDrawSourcesFunction().isEmpty()) {
                            JavaScriptObject functionPointer = AladinLiteWrapper.getAladinLite()
                                    .createFunctionPointer(descriptor.getDrawSourcesFunction());
                            details.put("shape", functionPointer);

                            // Adds the arrow style details
                            details.put("arrowColor", descriptor.getSecondaryColor());
                            details.put("arrowWidth", descriptor.getPmArrowWidth());
                            details.put("arrowLength", descriptor.getPmArrowWidth() * 6);
                        }
                    }

                    @Override
                    public void addSecondaryShape(GeneralJavaScriptObject rowData, String ra, String dec,
                            Map<String, String> details) {
                        if (descriptor.getDrawSourcesFunction() != null) {

                            // Adds the details for drawing the proper motion arrows
                            try {

                                Double finalRa = null;
                                Double finalDec = null;

                                if ((descriptor.getFinalRaTapColumn() != null)
                                        && descriptor.getFinalDecTapColumn() != null) {

                                    // Proper motion ra, dec is coming from descriptor data, just use it
                                    finalRa = rowData.getDoubleOrNullProperty(descriptor.getFinalRaTapColumn());
                                    finalDec = rowData.getDoubleOrNullProperty(descriptor.getFinalDecTapColumn());

                                } else {

                                    // Proper motion ra, dec not coming from descriptor data, so we need to
                                    // calculate it

                                    final Double pm_ra = rowData
                                            .getDoubleOrNullProperty(descriptor.getPmRaTapColumn());
                                    final Double pm_dec = rowData
                                            .getDoubleOrNullProperty(descriptor.getPmDecTapColumn());

                                    if ((pm_ra != null) && (pm_dec != null) && (descriptor.getPmOrigEpoch() != null)
                                            && (descriptor.getPmFinalEpoch() != null)) {

                                        double[] inputA = new double[6];
                                        inputA[0] = new Double(ra);
                                        inputA[1] = new Double(dec);
                                        inputA[2] = rowData.getDoubleProperty(descriptor.getPmPlxTapColumn()); // Consider
                                                                                                               // the
                                                                                                               // parallax
                                                                                                               // as
                                                                                                               // 0.0
                                                                                                               // by
                                                                                                               // default
                                        inputA[3] = pm_ra;
                                        inputA[4] = pm_dec;
                                        inputA[5] = rowData
                                                .getDoubleProperty(descriptor.getPmNormRadVelTapColumn()); // normalised
                                                                                                           // radial
                                                                                                           // velocity
                                                                                                           // at t0
                                                                                                           // [mas/yr]
                                                                                                           // - see
                                                                                                           // Note 2

                                        double[] outputA = new double[6];

                                        ProperMotionUtils.pos_prop(descriptor.getPmOrigEpoch(), inputA,
                                                descriptor.getPmFinalEpoch(), outputA);

                                        finalRa = outputA[0];
                                        finalDec = outputA[1];

                                        if (descriptor.getPmOrigEpoch() > descriptor.getPmFinalEpoch()) {
                                            // For catalogs in J2015 put the source in J2015 but draw the arrow
                                            // flipped... from J2000 to J2015
                                            details.put("arrowFlipped", "true");
                                        }
                                    }
                                }

                                if ((finalRa != null) && (finalDec != null)) {
                                    details.put("arrowRa", finalRa + "");
                                    details.put("arrowDec", finalDec + "");

                                    // Creates the ra dec normalized vector of 10 degrees
                                    final double raInc = finalRa - new Double(ra);
                                    final double decInc = finalDec - new Double(dec);
                                    final double m = Math.sqrt((raInc * raInc) + (decInc * decInc));
                                    final double mRatio = 2 / m; // 2 degrees
                                    final double raNorm = new Double(ra) + (raInc * mRatio);
                                    final double decNorm = new Double(dec) + (decInc * mRatio);

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
                                Log.error(this.getClass().getSimpleName() + ", Calculate proper motion error: ",
                                        ex);
                            }
                        }
                    }
                });

        addEntity(newCatEntity);

        return newCatEntity;
    }

    // PUBLICATIONS -------------

    public PublicationsEntity getPublications() {
        return publicationsEntity;
    }

    public PublicationsEntity createPublicationsEntity(PublicationsDescriptor descriptor) {

        String esaSkyUniqID = descriptor.generateId();
        SkyViewPosition skyViewPosition = CoordinateUtils.getCenterCoordinateInJ2000();

        publicationsEntity = new PublicationsEntity(descriptor,
                descriptorRepo.getPublicationsDescriptors().getCountStatus(), skyViewPosition,
                esaSkyUniqID);
        allEntities.add(publicationsEntity);

        return publicationsEntity;
    }

    // PUBLICATIONS BY SOURCE -------------

    public GeneralEntityInterface createPublicationsBySourceEntity(String sourceName, double ra, double dec, String bibcount) {
        final PublicationsDescriptor descriptor = descriptorRepo.getPublicationsDescriptors().getDescriptors().get(0);
        SkyViewPosition skyViewPosition = CoordinateUtils.getCenterCoordinateInJ2000();

        PublicationsBySourceEntity pubBySourceEntity = new PublicationsBySourceEntity(descriptor,
                descriptorRepo.getPublicationsDescriptors().getCountStatus(), skyViewPosition,
                sourceName, ra, dec, bibcount);
        allEntities.add(pubBySourceEntity);

        return pubBySourceEntity;
    }

    
    public GeneralEntityInterface createPublicationsByAuthorEntity(String author) {
        final PublicationsDescriptor descriptor = descriptorRepo.getPublicationsDescriptors().getDescriptors().get(0);
        SkyViewPosition skyViewPosition = CoordinateUtils.getCenterCoordinateInJ2000();

        GeneralEntityInterface pubByAuthorEntity = new PublicationsByAuthorEntity(descriptor,
                descriptorRepo.getPublicationsDescriptors().getCountStatus(), skyViewPosition,
                author);

        allEntities.add(pubByAuthorEntity);

        return pubByAuthorEntity;
    }
}