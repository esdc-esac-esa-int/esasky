package esac.archive.esasky.cl.web.client.repository;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.user.client.ui.Image;

import esac.archive.esasky.ifcs.model.coordinatesutils.SkyViewPosition;
import esac.archive.esasky.ifcs.model.descriptor.CatalogDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.CommonObservationDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.ExtTapDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.IDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.ObservationDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.PublicationsDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.SSODescriptor;
import esac.archive.esasky.cl.web.client.Modules;
import esac.archive.esasky.cl.web.client.model.entities.CatalogEntity;
import esac.archive.esasky.cl.web.client.model.entities.CombinedSourceFootprintDrawer;
import esac.archive.esasky.cl.web.client.model.entities.EntityContext;
import esac.archive.esasky.cl.web.client.model.entities.ExtTapEntity;
import esac.archive.esasky.cl.web.client.model.entities.ExtTapEntity.SecondaryShapeAdder;
import esac.archive.esasky.cl.web.client.model.entities.GeneralEntityInterface;
import esac.archive.esasky.cl.web.client.model.entities.PublicationsBySourceEntity;
import esac.archive.esasky.cl.web.client.model.entities.PublicationsEntity;
import esac.archive.esasky.cl.web.client.model.entities.SSOEntity;
import esac.archive.esasky.cl.web.client.query.AbstractMetadataService;
import esac.archive.esasky.cl.web.client.query.TAPExtTapService;
import esac.archive.esasky.cl.web.client.query.TAPMetadataCatalogueService;
import esac.archive.esasky.cl.web.client.query.TAPMetadataObservationService;
import esac.archive.esasky.cl.web.client.query.TAPMetadataSSOService;
import esac.archive.esasky.cl.web.client.query.TAPMetadataSurveyService;
import esac.archive.esasky.cl.web.client.model.SourceShapeType;
import esac.archive.esasky.cl.web.client.model.TapRowList;
import esac.archive.esasky.cl.web.client.status.CountStatus;
import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
import esac.archive.esasky.cl.web.client.utility.CoordinateUtils;
import esac.archive.esasky.cl.web.client.utility.ProperMotionUtils;
import esac.archive.esasky.cl.web.client.view.resultspanel.GeneralJavaScriptObject;

public class EntityRepository {

	private DescriptorRepository descriptorRepo;

	private PublicationsEntity publicationsEntity;
	private List<PublicationsBySourceEntity> publicationsBySourceEntities;
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

		if (Modules.publicationsModule) {
			publicationsBySourceEntities = new LinkedList<PublicationsBySourceEntity>();
		}
	}

	private List<GeneralEntityInterface> getAllEntities () {
		List<GeneralEntityInterface> totalEntities = new LinkedList<GeneralEntityInterface>(allEntities);
		if (Modules.publicationsModule) {
			totalEntities.addAll(publicationsBySourceEntities);
		}
		return totalEntities;
	}

	public GeneralEntityInterface getEntity (String id) {
		for (GeneralEntityInterface currEntity : getAllEntities()) {
			if (currEntity.getEsaSkyUniqId().equals(id)) {
				return currEntity;
			}
		}
		return null;
	}

	public void addEntity (GeneralEntityInterface newEntity) {
		if (newEntity != null) {
			allEntities.add(newEntity);
		}
	}

	public void removeEntity(GeneralEntityInterface entity) {
		allEntities.remove(entity);
	}
	
	public GeneralEntityInterface createEntity(IDescriptor descriptor) {
	    GeneralEntityInterface newEntity;
	    if(descriptor instanceof ObservationDescriptor && ((ObservationDescriptor)descriptor).getIsSurveyMission()) {
	        newEntity = new ExtTapEntity(descriptor, descriptorRepo.getObsDescriptors().getCountStatus(),
	                CoordinateUtils.getCenterCoordinateInJ2000(), descriptor.generateId(), TAPMetadataSurveyService.getInstance(), 
	                20, SourceShapeType.CROSS.getName());
	        addEntity(newEntity);   
	    } else if (descriptor instanceof SSODescriptor) {
	        return createEntity(descriptor, descriptorRepo.getSsoDescriptors().getCountStatus(), TAPMetadataSSOService.getInstance());
	    } else if (descriptor instanceof CommonObservationDescriptor) {
	        return createEntity(descriptor, descriptorRepo.getObsDescriptors().getCountStatus(), TAPMetadataObservationService.getInstance());
	    } else if (descriptor instanceof CatalogDescriptor) {
	        return createCatalogueEntity((CatalogDescriptor)descriptor);
	    } else if (descriptor instanceof ExtTapDescriptor) {
	        return createEntity(descriptor, descriptorRepo.getExtTapDescriptors().getCountStatus(), TAPExtTapService.getInstance());
	        
	    }
	    return null;//TODO SSO, Publication
	}
	
	private GeneralEntityInterface createEntity(IDescriptor descriptor, CountStatus countStatus, AbstractMetadataService metadataService) {
	    GeneralEntityInterface newEntity = new ExtTapEntity(descriptor, countStatus, CoordinateUtils.getCenterCoordinateInJ2000(), descriptor.generateId(), metadataService);
	    addEntity(newEntity);   
	    return newEntity;
	}

	// CATALOG -------------
	public GeneralEntityInterface createCatalogueEntity(final CatalogDescriptor descriptor) {
		String esaSkyUniqId = descriptor.generateId();
		SkyViewPosition skyViewPosition = CoordinateUtils.getCenterCoordinateInJ2000();
		GeneralEntityInterface newCatEntity = null;
		if(Modules.useTabulator) {
		    newCatEntity = new ExtTapEntity(descriptor, descriptorRepo.getCatDescriptors().getCountStatus(), 
		            skyViewPosition, esaSkyUniqId, TAPMetadataCatalogueService.getInstance(), new SecondaryShapeAdder() {
                        
                        @Override
                        public void createSpecializedOverlayShape(Map<String, Object> details) {
                            if (descriptor.getDrawSourcesFunction() != null && !descriptor.getDrawSourcesFunction().isEmpty()) {
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
                        public void addSecondaryShape(GeneralJavaScriptObject row, String ra, String dec, Map<String, String> details) {
                            if (descriptor.getDrawSourcesFunction() != null) {
                    
                                // Adds the details for drawing the proper motion arrows
                                try {
                    
                                    Double finalRa = null;
                                    Double finalDec = null;
                    
                                    if ((descriptor.getFinalRaTapColumn() != null)
                                            && descriptor.getFinalDecTapColumn() != null) {
                    
                                        // Proper motion ra, dec is coming from descriptor data, just use it
                                        finalRa = row.invokeFunction("getData").getDoubleProperty(descriptor.getFinalRaTapColumn());
                                        finalDec =row.invokeFunction("getData").getDoubleProperty(descriptor.getFinalDecTapColumn());
                    
                                    } else {
                    
                                        // Proper motion ra, dec not coming from descriptor data, so we need to
                                        // calculate it
                                        
                                        final Double pm_ra = row.invokeFunction("getData").getDoubleProperty(descriptor.getPmRaTapColumn());
                                        final Double pm_dec = row.invokeFunction("getData").getDoubleProperty(descriptor.getPmDecTapColumn());
                                        
                                        if ((pm_ra != null) && (pm_dec != null)
                                            && (descriptor.getPmOrigEpoch() != null)
                                            && (descriptor.getPmFinalEpoch() != null)) {
                                            
                                            double[] inputA = new double[6];
                                            inputA[0] = new Double(ra);
                                            inputA[1] = new Double(dec);
                                            inputA[2] = row.invokeFunction("getData").getDoubleProperty(descriptor.getPmPlxTapColumn()); // Consider the parallax as 0.0 by default
                                            inputA[3] = pm_ra;
                                            inputA[4] = pm_dec;
                                            inputA[5] = row.invokeFunction("getData").getDoubleProperty(descriptor.getPmNormRadVelTapColumn()); // normalised radial velocity at t0 [mas/yr] - see Note 2

                                            double[] outputA = new double[6];
                        
                                            ProperMotionUtils.pos_prop(descriptor.getPmOrigEpoch(), inputA,
                                                    descriptor.getPmFinalEpoch(), outputA);
                        
                                            finalRa = outputA[0];
                                            finalDec = outputA[1];
                                            
                                            if (descriptor.getPmOrigEpoch() > descriptor.getPmFinalEpoch()) {
                                                // For catalogs in J2015 put the source in J2015 but draw the arrow flipped... from J2000 to J2015
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
                                        
                                        //Calculates the scale factor, this function is also in AladinESDC.js (modify there also)
                                        final double arrowRatio = 4.0 - (3.0 * Math.pow(Math.log((m * 1000) + 2.72), -2.0)); // See: //rechneronline.de/function-graphs/  , using function: 4 - (3*(log((x* 1000)+2.72)^(-2)))
                                        
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
		} else {
		    Map<String, Object> catDetails = null;
		    if (descriptor.getDrawSourcesFunction() != null && !descriptor.getDrawSourcesFunction().isEmpty()) {
		        catDetails = new HashMap<String, Object>();
		        
		        JavaScriptObject functionPointer = AladinLiteWrapper.getAladinLite()
		                .createFunctionPointer(descriptor.getDrawSourcesFunction());
		        catDetails.put("shape", functionPointer);
		        
		        // Adds the arrow style details
		        catDetails.put("arrowColor", descriptor.getSecondaryColor());
		        catDetails.put("arrowWidth", descriptor.getPmArrowWidth());
		        catDetails.put("arrowLength", descriptor.getPmArrowWidth() * 6);
		    }
		    
		    JavaScriptObject catalogue = AladinLiteWrapper.getAladinLite().createCatalogWithDetails(
		            esaSkyUniqId, CombinedSourceFootprintDrawer.DEFAULT_SOURCE_SIZE, descriptor.getPrimaryColor(), catDetails);
		    newCatEntity = new CatalogEntity(descriptor,
		            descriptorRepo.getCatDescriptors().getCountStatus(), catalogue,
		            skyViewPosition, esaSkyUniqId);
		}
		
		
		allEntities.add(newCatEntity);
		
		return newCatEntity;
	}

	// PUBLICATIONS -------------
	private final Resources resources = GWT.create(Resources.class);

	public interface Resources extends ClientBundle {

		@Source("publications.png")
		@ImageOptions(flipRtl = true)
		ImageResource publicationIcon();

	}

	public PublicationsEntity getPublications() {
		return publicationsEntity;
	}

	private Map<String, Object> getPubDetails(){
		Map<String, Object> pubDetails = new HashMap<String, Object>();
		Image pubIcon = new Image(resources.publicationIcon());
		JavaScriptObject marker = AladinLiteWrapper.getAladinLite().createImageMarker(pubIcon.getUrl());

		pubDetails.put("shape", marker);
		return pubDetails;
	}

	public PublicationsEntity createPublicationsEntity(PublicationsDescriptor descriptor) {

		String esaSkyUniqID = descriptor.generateId();
		SkyViewPosition skyViewPosition = CoordinateUtils.getCenterCoordinateInJ2000();

		JavaScriptObject jsPubOverlay = AladinLiteWrapper.getInstance().createPublicationCatalogue(
				EntityContext.PUBLICATIONS.toString(), descriptor.getPrimaryColor(), getPubDetails());

		publicationsEntity = new PublicationsEntity(descriptor,
				descriptorRepo.getPublicationsDescriptors().getCountStatus(), jsPubOverlay, skyViewPosition,
				esaSkyUniqID);

		return publicationsEntity;
	}

	// PUBLICATIONS BY SOURCE -------------

	public PublicationsBySourceEntity createPublicationsBySourceEntity(String sourceId, boolean byAuthor) {

		final PublicationsDescriptor descriptor = descriptorRepo.getPublicationsDescriptors().getDescriptors().get(0);

		SkyViewPosition skyViewPosition = CoordinateUtils.getCenterCoordinateInJ2000();

		JavaScriptObject jsPubOverlay = AladinLiteWrapper.getAladinLite().createCatalogWithDetails(sourceId, 0,
				descriptor.getPrimaryColor(), getPubDetails());

		PublicationsBySourceEntity pubBySourceEntity = new PublicationsBySourceEntity(descriptor,
				descriptorRepo.getPublicationsDescriptors().getCountStatus(), jsPubOverlay, skyViewPosition,
				sourceId);

		if (!byAuthor) {

			// Adds the source related to this tab
			TapRowList sourceTapRowList = new TapRowList();
			sourceTapRowList.setMetadata(publicationsEntity.getMetadata().getMetadata());

			final int sourceIdx = publicationsEntity.getSourceIdx(sourceId);
			sourceTapRowList.addDataRow(publicationsEntity.getMetadata().getDataRow(sourceIdx));

			pubBySourceEntity.setSourceMetadata(sourceTapRowList);

			pubBySourceEntity.getSkyViewPosition().getCoordinate().ra = 
					Double.parseDouble(publicationsEntity.getMetadata().getDataValue("ra", sourceIdx));
			pubBySourceEntity.getSkyViewPosition().getCoordinate().dec = 
					Double.parseDouble(publicationsEntity.getMetadata().getDataValue("dec", sourceIdx));
		}

		publicationsBySourceEntities.add(pubBySourceEntity);

		return pubBySourceEntity;
	}

	public List<PublicationsBySourceEntity> getPublicationsBySource() {
		return publicationsBySourceEntities;
	}

	public PublicationsBySourceEntity getPublicationsBySourceEntity(String id) {
		for (PublicationsBySourceEntity currPubEntity : publicationsBySourceEntities) {
			if (currPubEntity.getEsaSkyUniqId().equals(id)) {
				return currPubEntity;
			}
		}
		return null;
	}

	public void removePublicationsBySourceEntity(String id) {
		PublicationsBySourceEntity removePubEntity = null;

		for (PublicationsBySourceEntity currPubEntity : publicationsBySourceEntities) {
			if (currPubEntity.getEsaSkyUniqId().equals(id)) {
				removePubEntity = currPubEntity;
				break;
			}
		}

		if (removePubEntity != null) {
			publicationsBySourceEntities.remove(removePubEntity);
		}
	}

	public void clearPublicationsBySource() {
		publicationsBySourceEntities.clear();
	}

	// SSO -------------

	public GeneralEntityInterface createSSOEntity(SSODescriptor descriptor) {

		String esaSkyUniqID = descriptor.generateId();
		SkyViewPosition skyViewPosition = CoordinateUtils.getCenterCoordinateInJ2000();

		return new SSOEntity(descriptor, descriptorRepo.getSsoDescriptors().getCountStatus(), skyViewPosition,
				esaSkyUniqID);
	}
}