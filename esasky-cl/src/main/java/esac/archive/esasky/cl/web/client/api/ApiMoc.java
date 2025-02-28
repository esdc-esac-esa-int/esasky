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

package esac.archive.esasky.cl.web.client.api;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.JavaScriptObject;
import esac.archive.esasky.cl.web.client.Controller;
import esac.archive.esasky.cl.web.client.model.LineStyle;
import esac.archive.esasky.cl.web.client.model.entities.MOCEntity;
import esac.archive.esasky.cl.web.client.repository.MocRepository;
import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;
import esac.archive.esasky.ifcs.model.descriptor.CommonTapDescriptor;
import esac.archive.esasky.cl.web.client.view.resultspanel.ITablePanel;

public class ApiMoc extends ApiBase{
	

	public ApiMoc(Controller controller) {
		this.controller = controller;
	}
	
	public void addMOC(String name, GeneralJavaScriptObject options, GeneralJavaScriptObject mocData) {
		MOCEntity old = MocRepository.getInstance().getEntityByName(name);
		if(old != null) {
			old.closeFromAPI();
			MocRepository.getInstance().removeEntity(old);
		}

		CommonTapDescriptor descriptor = controller.getRootPresenter().getDescriptorRepository()
				.initUserDescriptor4MOC(name, options);
		MOCEntity entity = new MOCEntity(descriptor);

		if(options.hasProperty(ApiConstants.MOC_LINE_STYLE)) {
			entity.setLineStyle(options.getStringProperty(ApiConstants.MOC_LINE_STYLE));
		}else {
			entity.setLineStyle(LineStyle.SOLID.getName());
		}

		if(options.hasProperty(ApiConstants.MOC_OPACITY)) {
			entity.setSizeRatio(options.getDoubleProperty(ApiConstants.MOC_OPACITY));
		}

		if(!options.hasProperty(ApiConstants.MOC_MODE)) {
			options.setProperty(ApiConstants.MOC_MODE, ApiConstants.MOC_HEALPIX);
		}

		if(options.hasProperty(ApiConstants.MOC_ADD_TAB)
				&& GeneralJavaScriptObject.convertToBoolean(options.getProperty(ApiConstants.MOC_ADD_TAB))) {
			ITablePanel panel = controller.getRootPresenter().getResultsPresenter().addResultsTab(entity);
			entity.setTablePanel(panel);
			panel.setEmptyTable("Showing coverage of " + name);
		}
		entity.addJSON(mocData, options);
		
	}
	
	public void removeMOC(String name) {
		MOCEntity entity = MocRepository.getInstance().getEntityByName(name);
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
