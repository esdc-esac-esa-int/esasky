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

package esac.archive.esasky.cl.web.client.view.resultspanel.tabulator;

import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONNull;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;

public class TabulatorSettings{
	private Boolean addSendToVOApplicationColumn = false;
	private Boolean addLink2ArchiveColumn = false;
	private Boolean addLink2AdsColumn = false;
	private Boolean addSourcesInPublicationColumn = false;
	private Boolean addTimeVizColumn = false;
	private String timeVizWhereQuery = "";
	private String selectionHeaderTitle = null; // Selection Title if selection column is enabled. null = no text
	private Boolean blockRedraw = false; // block initial draw operation - restoreRedraw has to be called manually afterwards
    private Boolean isEsaskyData = true; // Data from user or not
    private Boolean addSelectionColumn = false;
    private Boolean addDatalinkLink2ArchiveColumn = false;
    private Integer selectable = null; // number of rows that can be selected simultaneously. null = no limit
	private Boolean disableGoToColumn = false; // true to force remove goto column, even if there is a ra an dec column
	private Boolean useUcd = false;
	private Boolean isDownloadable = true;

	private  Boolean addAdqlColumn = false;
	private Boolean addMetadataColumn = false;
	private Boolean addOpenTableColumn = false;
	private String tableLayout = "fitDataFill";
	private Boolean addObscoreTableColumn = false;
	private Boolean showDetailedErrors = false;
	private Boolean fovLimitDisabled = false;
	private Boolean deleteRowColumn = false;
	private Boolean editRowColumn = false;
	private String customColumn;
	private Boolean customMetadata = false;
	private Boolean showCreateRowButton = false;
	private String createRowButtonTooltip = "Add row";
	private String createRowButtonIcon = "images/plus-sign-light-small.png";

	public String convertToJsonString() {
		JSONObject json = new JSONObject();
		json.put("addSendToVOApplicationColumn", JSONBoolean.getInstance(addSendToVOApplicationColumn));
		json.put("addLink2ArchiveColumn", JSONBoolean.getInstance(addLink2ArchiveColumn));
		json.put("addLink2AdsColumn", JSONBoolean.getInstance(addLink2AdsColumn));
		json.put("addSourcesInPublicationColumn", JSONBoolean.getInstance(addSourcesInPublicationColumn));
		json.put("addTimeVizColumn", JSONBoolean.getInstance(addTimeVizColumn));
		json.put("timeVizWhereQuery", new JSONString(timeVizWhereQuery));
		if(selectionHeaderTitle == null) {
			json.put("selectionHeaderTitle", JSONNull.getInstance());
		} else {
			json.put("selectionHeaderTitle", new JSONString(selectionHeaderTitle));
		}
		json.put("blockRedraw", JSONBoolean.getInstance(blockRedraw));
		json.put("isEsaskyData", JSONBoolean.getInstance(isEsaskyData));
		json.put("addSelectionColumn", JSONBoolean.getInstance(addSelectionColumn));
		json.put("addDatalinkLink2ArchiveColumn", JSONBoolean.getInstance(addDatalinkLink2ArchiveColumn));
		if(selectable == null) {
			json.put("selectable", JSONNull.getInstance());
		} else {
			json.put("selectable", new JSONNumber(selectable));
		}
		json.put("disableGoToColumn", JSONBoolean.getInstance(disableGoToColumn));
		json.put("useUcd", JSONBoolean.getInstance(useUcd));
		json.put("isDownloadable", JSONBoolean.getInstance(isDownloadable));
		json.put("addAdqlColumn", JSONBoolean.getInstance(addAdqlColumn));
		json.put("addMetadataColumn", JSONBoolean.getInstance(addMetadataColumn));
		json.put("tableLayout", new JSONString(tableLayout));
		json.put("addObscoreTableColumn", JSONBoolean.getInstance(addObscoreTableColumn));
		json.put("showDetailedErrors", JSONBoolean.getInstance(showDetailedErrors));
		json.put("fovLimitDisabled", JSONBoolean.getInstance(fovLimitDisabled));
		json.put("addOpenTableColumn", JSONBoolean.getInstance(addOpenTableColumn));
		json.put("deleteRowColumn", JSONBoolean.getInstance(deleteRowColumn));
		json.put("editRowColumn", JSONBoolean.getInstance(editRowColumn));
		json.put("customMetadata", JSONBoolean.getInstance(customMetadata));
		json.put("showCreateRowButton", JSONBoolean.getInstance(showCreateRowButton));
		json.put("showCreateRowButtonTooltip", new JSONString(createRowButtonTooltip));
		json.put("showCreateRowButtonIcon", new JSONString(createRowButtonIcon));
		if (customColumn != null) {
			json.put("customColumn", new JSONString(customColumn));
		}

		return json.toString();
	}

	public Boolean getAddSendToVOApplicationColumn() {
		return addSendToVOApplicationColumn;
	}

	public void setAddSendToVOApplicationColumn(Boolean addSendToVOApplicationColumn) {
		this.addSendToVOApplicationColumn = addSendToVOApplicationColumn;
	}

	public Boolean getAddLink2ArchiveColumn() {
		return addLink2ArchiveColumn;
	}

	public void setAddLink2ArchiveColumn(Boolean addLink2ArchiveColumn) {
		this.addLink2ArchiveColumn = addLink2ArchiveColumn;
	}

	public Boolean getAddLink2AdsColumn() {
		return addLink2AdsColumn;
	}

	public void setAddLink2AdsColumn(Boolean addLink2AdsColumn) {
		this.addLink2AdsColumn = addLink2AdsColumn;
	}

	public Boolean getAddSourcesInPublicationColumn() {
		return addSourcesInPublicationColumn;
	}

	public void setAddSourcesInPublicationColumn(Boolean addSourcesInPublicationColumn) {
		this.addSourcesInPublicationColumn = addSourcesInPublicationColumn;
	}
	
	public Boolean getAddTimeVizColumn() {
		return addTimeVizColumn;
	}

	public void setAddTimeVizColumn(Boolean addTimeVizColumn) {
		this.addTimeVizColumn = addTimeVizColumn;
	}

	public void setTimeVizWhereQuery(String timeVizWhereQuery) {
		this.timeVizWhereQuery = timeVizWhereQuery;
	}

	public String getSelectionHeaderTitle() {
		return selectionHeaderTitle;
	}

	public void setSelectionHeaderTitle(String selectionHeaderTitle) {
		this.selectionHeaderTitle = selectionHeaderTitle;
	}

	public Boolean getBlockRedraw() {
		return blockRedraw;
	}

	public void setBlockRedraw(Boolean blockRedraw) {
		this.blockRedraw = blockRedraw;
	}

	public Boolean getEsaskyData() {
		return isEsaskyData;
	}

	public void setEsaskyData(Boolean esaskyData) {
		isEsaskyData = esaskyData;
	}

	public Boolean getAddSelectionColumn() {
		return addSelectionColumn;
	}

	public void setAddSelectionColumn(Boolean addSelectionColumn) {
		this.addSelectionColumn = addSelectionColumn;
	}

	public Boolean getAddDatalinkLink2ArchiveColumn() {
		return addDatalinkLink2ArchiveColumn;
	}

	public void setAddDatalinkLink2ArchiveColumn(Boolean addDatalinkLink2ArchiveColumn) {
		this.addDatalinkLink2ArchiveColumn = addDatalinkLink2ArchiveColumn;
	}

	public Integer getSelectable() {
		return selectable;
	}

	public void setSelectable(Integer selectable) {
		this.selectable = selectable;
	}

	public Boolean getDisableGoToColumn() {
		return disableGoToColumn;
	}

	public void setDisableGoToColumn(Boolean disableGoToColumn) {
		this.disableGoToColumn = disableGoToColumn;
	}

	public Boolean getUseUcd() {
		return useUcd;
	}

	public void setUseUcd(Boolean useUcd) {
		this.useUcd = useUcd;
	}

	public Boolean getIsDownloadable() {
		return isDownloadable;
	}

	public void setIsDownloadable(Boolean isDownloadable) {
		this.isDownloadable = isDownloadable;
	}

	public void setAddAdqlColumn(boolean addAdqlColumn) {
		this.addAdqlColumn = addAdqlColumn;
	}
	
	public void setAddMetadataColumn(boolean addMetadataColumn) {
		this.addMetadataColumn = addMetadataColumn;
	}

	public String getTableLayout() {
		return tableLayout;
	}

	public void setTableLayout(String tableLayout) {
		this.tableLayout = tableLayout;
	}

	public void setAddObscoreTableColumn(boolean addObscoreTableColumn) {
		this.addObscoreTableColumn = addObscoreTableColumn;
	}

	public Boolean getShowDetailedErrors() {
		return showDetailedErrors;
	}

	public void setShowDetailedErrors(Boolean showDetailedErrors) {
		this.showDetailedErrors = showDetailedErrors;
	}

	public Boolean getFovLimiterDisabled() {
		return fovLimitDisabled;
	}

	public void setFovLimiterDisabled(Boolean fovLimitDisabled) {
		this.fovLimitDisabled = fovLimitDisabled;
	}

	public Boolean getAddOpenTableColumn() {
		return addOpenTableColumn;
	}

	public void setAddOpenTableColumn(Boolean addOpenTableColumn) {
		this.addOpenTableColumn = addOpenTableColumn;
	}

	public void setDeleteRowColumn(Boolean deleteRowColumn) {
		this.deleteRowColumn = deleteRowColumn;
	}

	public Boolean getDeleteRowColumn() {
		return deleteRowColumn;
	}

	public Boolean getEditRowColumn() {
		return editRowColumn;
	}

	public void setEditRowColumn(Boolean editRowColumn) {
		this.editRowColumn = editRowColumn;
	}

	public String getCustomColumn() {
		return customColumn;
	}

	public void setCustomColumn(String customColumn) {
		this.customColumn = customColumn;
	}

	public Boolean getCustomMetadata() {
		return customMetadata;
	}

	public void setCustomMetadata(Boolean customMetadata) {
		this.customMetadata = customMetadata;
	}

	public Boolean getShowCreateRowButton() {
		return showCreateRowButton;
	}

	public void setShowCreateRowButton(Boolean showCreateRowButton) {
		this.showCreateRowButton = showCreateRowButton;
	}

	public void setShowCreateRowButton(Boolean showCreateRowButton, String tooltip) {
		this.showCreateRowButton = showCreateRowButton;
		this.createRowButtonTooltip = tooltip;
	}

	public void setShowCreateRowButton(Boolean showCreateRowButton, String tooltip, String icon) {
		this.showCreateRowButton = showCreateRowButton;
		this.createRowButtonTooltip = tooltip;
		this.createRowButtonIcon = icon;
	}

}