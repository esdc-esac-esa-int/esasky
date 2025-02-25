/*
ESASky
Copyright (C) 2025 Henrik Norman

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
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.resources.client.ImageResource;
import esac.archive.esasky.cl.web.client.Controller;
import esac.archive.esasky.cl.web.client.Modules;
import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
import esac.archive.esasky.cl.web.client.utility.GoogleAnalytics;
import esac.archive.esasky.cl.web.client.utility.exceptions.MapKeyException;
import esac.archive.esasky.cl.web.client.view.common.buttons.EsaSkyButton;
import esac.archive.esasky.cl.web.client.view.common.icons.Icons;
import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;
import esac.archive.esasky.ifcs.model.descriptor.CommonTapDescriptor;
import esac.archive.esasky.ifcs.model.descriptor.CustomTreeMapDescriptor;

import java.util.LinkedList;
import java.util.List;

public class ApiModules extends ApiBase{
	
	private ApiEvents apiEvents;
	
	private final String MISSING_TREEMAP_PROPERTY = "Missing treeMap property name";
	private final String MISSING_BUTTON_PROPERTY = "Missing button property name";
	private final String WRONG_BUTTON_ICON = "Wrong button icon name";
	
	public ApiModules(Controller controller, ApiEvents apiEvents) {
		this.controller = controller;
		this.apiEvents = apiEvents;
	}
	
	public void showCoordinateGrid(boolean show) {
		GoogleAnalytics.sendEvent(googleAnalyticsCat, GoogleAnalytics.ACT_PYESASKY_SHOWCOORDINATEGRID,Boolean.toString(show));
		AladinLiteWrapper.getInstance().toggleGrid(show);
	}
	
	private class ButtonInput{
		String name;
		ImageResource icon;
		String iconText;
		String description;
		boolean hasError = false;
		
		ButtonInput(GeneralJavaScriptObject input, JavaScriptObject widget){
			name = "";
			if(input.hasProperty(ApiConstants.BUTTON_NAME)) {
				name = input.getStringProperty(ApiConstants.BUTTON_NAME);
				
			} else {
				sendBackErrorMsgToWidget(MISSING_BUTTON_PROPERTY, widget);
				hasError = true;
			}
			
			description = "";
			if(input.hasProperty(ApiConstants.BUTTON_DESCRIPTION)) {
				description = input.getStringProperty(ApiConstants.BUTTON_DESCRIPTION);
			}
			
			iconText = name;
			if(input.hasProperty(ApiConstants.BUTTON_ICON_TEXT)) {
				iconText = input.getStringProperty(ApiConstants.BUTTON_ICON_TEXT);
			}
			
			icon = null;
			if(input.hasProperty(ApiConstants.BUTTON_ICON)) {
				String iconName = input.getStringProperty(ApiConstants.BUTTON_ICON);
				if(Icons.getIconMap().containsKey(iconName)) {
					icon = Icons.getIconMap().get(iconName);
				}else {
					JSONObject error = new JSONObject();
					error.put(ApiConstants.MESSAGE, new JSONString(WRONG_BUTTON_ICON));
					error.put(ApiConstants.ERROR_AVAILABLE, new JSONString(Icons.getIconMap().keySet().toString()));
					sendBackErrorToWidget(error, widget);
					hasError = true;
				}
			}
		}
		
	}

	public void addCustomButton(GeneralJavaScriptObject input, JavaScriptObject widget) {
		
		ButtonInput parsedInputs = new ButtonInput(input, widget);
		if(parsedInputs.hasError) {
			return;
		}
		
		EsaSkyButton button = controller.getRootPresenter().getCtrlTBPresenter().addCustomButton(parsedInputs.name, parsedInputs.icon,
				parsedInputs.iconText, parsedInputs.description);
		final String returnName = parsedInputs.name;
		button.addClickHandler(event -> apiEvents.ctrlBarButtonClicked(returnName));
		
	}

	public void updateCustomButton(GeneralJavaScriptObject input, JavaScriptObject widget) {

		ButtonInput parsedInputs = new ButtonInput(input, widget);
		if(parsedInputs.hasError) {
			return;
		}
		
		controller.getRootPresenter().getCtrlTBPresenter().updateCustomButton(parsedInputs.name, parsedInputs.icon,
				parsedInputs.iconText, parsedInputs.description);
		
	}
	public void removeCustomButton(GeneralJavaScriptObject input, JavaScriptObject widget) {
		ButtonInput parsedInputs = new ButtonInput(input, widget);
		if(parsedInputs.hasError) {
			return;
		}
		controller.getRootPresenter().getCtrlTBPresenter().removeCustomButton(parsedInputs.name);
		
	}

	public void addCustomTreeMap(GeneralJavaScriptObject input, JavaScriptObject widget) {
		String treeMapName = "";
		if(input.hasProperty(ApiConstants.TREEMAP_NAME_OLD)) {
			treeMapName = input.getStringProperty(ApiConstants.TREEMAP_NAME_OLD);
			
		}else if(input.hasProperty(ApiConstants.TREEMAP_NAME)) {
				treeMapName = input.getStringProperty(ApiConstants.TREEMAP_NAME);
		
		} else {
			sendBackErrorMsgToWidget(MISSING_TREEMAP_PROPERTY, widget);
			return;
		}
		
		String description = "";
		if(input.hasProperty(ApiConstants.TREEMAP_DESCRIPTION)) {
			description = input.getStringProperty(ApiConstants.TREEMAP_DESCRIPTION);
		}
		
		String iconText = treeMapName;
		if(input.hasProperty(ApiConstants.TREEMAP_ICON_TEXT)) {
			iconText = input.getStringProperty(ApiConstants.TREEMAP_ICON_TEXT);
		}
		
		GeneralJavaScriptObject descriptorArray = input.getProperty(ApiConstants.TREEMAP_MISSIONS);
		if(descriptorArray == null) {
			sendBackErrorMsgToWidget(MISSING_TREEMAP_PROPERTY, widget);
			return;
		}
		
		List<CommonTapDescriptor> descriptors = createTreeMapDescriptors(widget, descriptorArray);
		if(descriptors.isEmpty()) {
		    return;
		}
		
		CustomTreeMapDescriptor customTreeMapDescriptor = new CustomTreeMapDescriptor(treeMapName, description, iconText, descriptors);
		
		customTreeMapDescriptor.setOnMissionClicked(mission -> apiEvents.treeMapClicked(customTreeMapDescriptor.getName(), mission));
		
		controller.getRootPresenter().getCtrlTBPresenter().addCustomTreeMap(customTreeMapDescriptor);
	}
	
	public void updateTreeMapMission(GeneralJavaScriptObject input, boolean add, JavaScriptObject widget) {
		
		String treeMapName = "";
		if(input.hasProperty(ApiConstants.TREEMAP_NAME_OLD)) {
			treeMapName = input.getStringProperty(ApiConstants.TREEMAP_NAME_OLD);
			
		}else if(input.hasProperty(ApiConstants.TREEMAP_NAME)) {
				treeMapName = input.getStringProperty(ApiConstants.TREEMAP_NAME);
		
		} else {
			sendBackErrorMsgToWidget("Missing treeMap property name", widget);
			return;
		}
		
		GeneralJavaScriptObject descriptorArray = input.getProperty(ApiConstants.TREEMAP_MISSIONS);
		if(descriptorArray == null) {
			sendBackErrorMsgToWidget("Missing treeMap missions", widget);
			return;
		}
		
		CustomTreeMapDescriptor customTreeMapDescriptor = controller.getRootPresenter().getCtrlTBPresenter().getCustomTreeMapDescriptor(treeMapName);
		List<CommonTapDescriptor> newMissions = createTreeMapDescriptors(widget, descriptorArray);
		if(add) {
			customTreeMapDescriptor.getMissionDescriptors().addAll(newMissions);
		}else {
			for(CommonTapDescriptor missionToRemove : newMissions) {
				customTreeMapDescriptor.getMissionDescriptors().removeIf(existingMission -> missionToRemove.getLongName().equals(existingMission.getLongName()));
			}
		}
		controller.getRootPresenter().getCtrlTBPresenter().updateCustomTreeMap(customTreeMapDescriptor);
		
	}

    private List<CommonTapDescriptor> createTreeMapDescriptors(JavaScriptObject widget,
            GeneralJavaScriptObject descriptorArray) {
        List<CommonTapDescriptor> descriptors = new LinkedList<>();
		int i = 0;
		while(descriptorArray.hasProperty(Integer.toString(i))) {
			
			GeneralJavaScriptObject mission = descriptorArray.getProperty(Integer.toString(i));
			
			CommonTapDescriptor descriptor = new CommonTapDescriptor();
			
			String missionName = "";
			if(mission.hasProperty(ApiConstants.TREEMAP_MISSION_NAME)) {
				missionName = mission.getStringProperty(ApiConstants.TREEMAP_MISSION_NAME);
			} else {
				sendBackErrorMsgToWidget("Missing mission property \"" + ApiConstants.TREEMAP_MISSION_NAME + "\"", widget);
				return descriptors;
			}
			
			String color = "";
			if(mission.hasProperty(ApiConstants.TREEMAP_MISSION_COLOR)) {
				color = mission.getStringProperty(ApiConstants.TREEMAP_MISSION_COLOR);
			} else {
				sendBackErrorMsgToWidget("Missing mission property \"" + ApiConstants.TREEMAP_MISSION_COLOR +"\"", widget);
				return descriptors;
			}
			
			descriptor.setMission(missionName);
			descriptor.setShortName(missionName);
			descriptor.setLongName(missionName);
			descriptor.setColor(color);
			descriptor.setSampEnabled(false);
			descriptor.setFovLimit(360.0);
			descriptor.setTableName("<not_set>");
			descriptors.add(descriptor);
			i++;
		}
        return descriptors;
    }
	
	public void setModuleVisibility(GeneralJavaScriptObject data, JavaScriptObject widget) {
		try {
			String propertiesString = data.getProperties();
			String[] properties = {propertiesString};
			if(propertiesString.contains(",")) {
				properties = propertiesString.split(",");
			}
			for(String key : properties) {
				Modules.setModule(key, GeneralJavaScriptObject.convertToBoolean(data.getProperty(key)));
			}
			controller.getRootPresenter().updateModuleVisibility();
			
		}catch(MapKeyException e) {
			
			Log.debug("[ApiModules]" + e.getMessage(), e);
			
			String message = "Input needs to be on format {key:boolean}";
			JSONObject error = new JSONObject();
			error.put(ApiConstants.MESSAGE, new JSONString(message));
			
			JSONArray availableModules = getAvailableModules();
			error.put(ApiConstants.ERROR_AVAILABLE, availableModules);
			sendBackErrorToWidget(error, widget);
		}
	}
	
	public void getAvailableModules(JavaScriptObject widget) {
		JSONArray availableModules = getAvailableModules();
		JSONObject obj = new JSONObject();
		obj.put(ApiConstants.ERROR_AVAILABLE, availableModules);
		sendBackValuesToWidget(obj, widget);
	}
	
	public JSONArray getAvailableModules() {
		JSONArray availableModules = new JSONArray();
		int i = 0;
		for(String module : Modules.getModuleKeys()) {
			availableModules.set(i, new JSONString(module));
			i++;
		}
		return availableModules;
	}
}
