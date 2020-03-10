package esac.archive.esasky.cl.web.client.repository;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
import esac.archive.esasky.ifcs.model.descriptor.ObservationDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.PublicationsDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.SSODescriptor;
import esac.archive.esasky.ifcs.model.descriptor.SpectraDescriptor;
import esac.archive.esasky.cl.web.client.Modules;
import esac.archive.esasky.cl.web.client.model.entities.CatalogEntity;
import esac.archive.esasky.cl.web.client.model.entities.CombinedSourceFootprintDrawer;
import esac.archive.esasky.cl.web.client.model.entities.EntityContext;
import esac.archive.esasky.cl.web.client.model.entities.ExtTapEntity;
import esac.archive.esasky.cl.web.client.model.entities.GeneralEntityInterface;
import esac.archive.esasky.cl.web.client.model.entities.ObservationEntity;
import esac.archive.esasky.cl.web.client.model.entities.PublicationsBySourceEntity;
import esac.archive.esasky.cl.web.client.model.entities.PublicationsEntity;
import esac.archive.esasky.cl.web.client.model.entities.SSOEntity;
import esac.archive.esasky.cl.web.client.model.entities.SpectraEntity;
import esac.archive.esasky.cl.web.client.model.entities.SurveyEntity;
import esac.archive.esasky.cl.web.client.model.TapRowList;
import esac.archive.esasky.cl.web.client.status.CountStatus;
import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
import esac.archive.esasky.cl.web.client.utility.CoordinateUtils;

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
	
	public GeneralEntityInterface createCommonObservationEntity(CommonObservationDescriptor descriptor, EntityContext context) {
		String esaSkyUniqID = descriptor.generateId();
		SkyViewPosition skyViewPosition = CoordinateUtils.getCenterCoordinateInJ2000();

		GeneralEntityInterface newEntity = null;
		// TODO move this code into a factory class
		if (descriptor instanceof ObservationDescriptor) {
			if (((ObservationDescriptor) descriptor).getIsSurveyMission()) {
				newEntity = createSurveyMissionEntity((ObservationDescriptor) descriptor, esaSkyUniqID,
						skyViewPosition, context, descriptorRepo.getObsDescriptors().getCountStatus());
			} else {
				newEntity = createObservationEntity(descriptor, esaSkyUniqID, skyViewPosition, context,
						descriptorRepo.getObsDescriptors().getCountStatus());
			}
		} else if (descriptor instanceof SpectraDescriptor) {
			newEntity = createSpectraEntity(descriptor, esaSkyUniqID, skyViewPosition, context,
					descriptorRepo.getSpectraDescriptors().getCountStatus());
		} else {
			throw new IllegalArgumentException("Unknown Descriptor type");
		}
		addEntity(newEntity);	
		return newEntity;
	}

	private GeneralEntityInterface createObservationEntity(CommonObservationDescriptor descriptor,
			String esaSkyUniqID, SkyViewPosition skyViewPosition, EntityContext entityContext, CountStatus countStatus) {
		return new ObservationEntity(descriptor, countStatus,
				skyViewPosition, esaSkyUniqID, System.currentTimeMillis(),
				entityContext);
	}

	private GeneralEntityInterface createSpectraEntity(CommonObservationDescriptor descriptor,
			String esaSkyUniqID, SkyViewPosition skyViewPosition, EntityContext entityContext, CountStatus countStatus) {
		return new SpectraEntity(descriptor, countStatus,
				skyViewPosition, esaSkyUniqID, System.currentTimeMillis(),
				entityContext);
	}

	private GeneralEntityInterface createSurveyMissionEntity(ObservationDescriptor descriptor,
			String esaSkyUniqID, SkyViewPosition skyViewPosition, EntityContext entityContext, CountStatus countStatus) {
		return new SurveyEntity(descriptor, countStatus,
				skyViewPosition, esaSkyUniqID, System.currentTimeMillis(),
				entityContext);
	}

	// CATALOG -------------
	public GeneralEntityInterface createCatalogueEntity(CatalogDescriptor catDescriptor, EntityContext context) {
		String esaSkyUniqID = catDescriptor.generateId();
		SkyViewPosition skyViewPosition = CoordinateUtils.getCenterCoordinateInJ2000();

		Map<String, Object> catDetails = null;
		if (catDescriptor.getDrawSourcesFunction() != null && !catDescriptor.getDrawSourcesFunction().isEmpty()) {
			catDetails = new HashMap<String, Object>();

			JavaScriptObject functionPointer = AladinLiteWrapper.getAladinLite()
					.createFunctionPointer(catDescriptor.getDrawSourcesFunction());
			catDetails.put("shape", functionPointer);

			// Adds the arrow style details
			catDetails.put("arrowColor", catDescriptor.getPmArrowColor());
			catDetails.put("arrowWidth", catDescriptor.getPmArrowWidth());
			catDetails.put("arrowLength", catDescriptor.getPmArrowWidth() * 6);
		}

		JavaScriptObject catalogue = AladinLiteWrapper.getAladinLite().createCatalogWithDetails(
				esaSkyUniqID, CombinedSourceFootprintDrawer.DEFAULT_SOURCE_SIZE, catDescriptor.getHistoColor(), catDetails);
		CatalogEntity newCatEntity = null;
		newCatEntity = new CatalogEntity(catDescriptor,
				descriptorRepo.getCatDescriptors().getCountStatus(), catalogue,
				skyViewPosition, esaSkyUniqID,
				System.currentTimeMillis(), context);

		if (newCatEntity != null) {
			allEntities.add(newCatEntity);
		}
		
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
				EntityContext.PUBLICATIONS.toString(), descriptor.getHistoColor(), getPubDetails());

		publicationsEntity = new PublicationsEntity(descriptor,
				descriptorRepo.getPublicationsDescriptors().getCountStatus(), jsPubOverlay, skyViewPosition,
				esaSkyUniqID, System.currentTimeMillis(), EntityContext.PUBLICATIONS);

		return publicationsEntity;
	}

	// PUBLICATIONS BY SOURCE -------------

	public PublicationsBySourceEntity createPublicationsBySourceEntity(String sourceId, boolean byAuthor) {

		final PublicationsDescriptor descriptor = descriptorRepo.getPublicationsDescriptors().getDescriptors().get(0);

		SkyViewPosition skyViewPosition = CoordinateUtils.getCenterCoordinateInJ2000();

		JavaScriptObject jsPubOverlay = AladinLiteWrapper.getAladinLite().createCatalogWithDetails(sourceId, 0,
				descriptor.getHistoColor(), getPubDetails());

		PublicationsBySourceEntity pubBySourceEntity = new PublicationsBySourceEntity(descriptor,
				descriptorRepo.getPublicationsDescriptors().getCountStatus(), jsPubOverlay, skyViewPosition,
				sourceId, System.currentTimeMillis(), EntityContext.PUBLICATIONS);

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
				esaSkyUniqID, System.currentTimeMillis(), EntityContext.SSO);
	}
	
	public GeneralEntityInterface createExtTapEntity(ExtTapDescriptor descriptor, EntityContext context) {
		String esaSkyUniqID = descriptor.generateId();
		SkyViewPosition skyViewPosition = CoordinateUtils.getCenterCoordinateInJ2000();
		CountStatus countStatus = descriptorRepo.getExtTapDescriptors().getCountStatus();
		ExtTapEntity newEntity = new ExtTapEntity(descriptor, countStatus,
				skyViewPosition, esaSkyUniqID, System.currentTimeMillis(),
				context);
		addEntity(newEntity);	
		return newEntity;
	}
}