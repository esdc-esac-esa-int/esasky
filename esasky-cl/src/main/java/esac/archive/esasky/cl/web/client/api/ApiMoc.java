package esac.archive.esasky.cl.web.client.api;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.JavaScriptObject;
import esac.archive.esasky.cl.web.client.Controller;
import esac.archive.esasky.cl.web.client.model.entities.MOCEntity;
import esac.archive.esasky.cl.web.client.repository.MocRepository;
import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;

public class ApiMoc extends ApiBase{
	

	public ApiMoc(Controller controller) {
		this.controller = controller;
	}
	
	public void addMOC(String name, GeneralJavaScriptObject options, GeneralJavaScriptObject mocData) {
		// TODO: fix
//		MOCEntity old = MocRepository.getInstance().getEntity(name);
//		if(old != null) {
//			old.closeFromAPI();
//			MocRepository.getInstance().removeEntity(old);
//		}
//
//		IDescriptor descriptor = controller.getRootPresenter().getDescriptorRepository()
//				.initUserDescriptor4MOC(name, options);
//		MOCEntity entity = new MOCEntity(descriptor);
//
//		if(options.hasProperty(ApiConstants.MOC_LINE_STYLE)) {
//			entity.setLineStyle(options.getStringProperty(ApiConstants.MOC_LINE_STYLE));
//		}else {
//			entity.setLineStyle(LineStyle.SOLID.getName());
//		}
//
//		if(options.hasProperty(ApiConstants.MOC_OPACITY)) {
//			entity.setSizeRatio(options.getDoubleProperty(ApiConstants.MOC_OPACITY));
//		}
//
//		if(!options.hasProperty(ApiConstants.MOC_MODE)) {
//			options.setProperty(ApiConstants.MOC_MODE, ApiConstants.MOC_HEALPIX);
//		}
//
//		if(options.hasProperty(ApiConstants.MOC_ADD_TAB)
//				&& GeneralJavaScriptObject.convertToBoolean(options.getProperty(ApiConstants.MOC_ADD_TAB))) {
//			ITablePanel panel = controller.getRootPresenter().getResultsPresenter().addResultsTab(entity);
//			entity.setTablePanel(panel);
//			panel.setEmptyTable("Showing coverage of " + name);
//		}
//		entity.addJSON(mocData, options);
		
	}
	
	public void removeMOC(String name) {
		MOCEntity entity = MocRepository.getInstance().getEntity(name);
		if(entity != null) {
			entity.closeFromAPI();
			MocRepository.getInstance().removeEntity(entity);
		}
	}
	
	public void addQ3CMOC(String options, String mocData) {
		JavaScriptObject moc = AladinLiteWrapper.getAladinLite().createQ3CMOC(options);
		AladinLiteWrapper.getAladinLite().addMOCData(moc, mocData);
		AladinLiteWrapper.getAladinLite().addMOC(moc);
	}
	
	public void getVisibleNpix(int norder) {
		JavaScriptObject js = AladinLiteWrapper.getAladinLite().getVisibleNpix(norder);
		Log.debug(js.toString());
	}
}
