package esac.archive.esasky.cl.web.client.view.resultspanel;

import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONNull;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;

public class TabulatorSettings{
	public Boolean addSendToVOApplicationColumn = false;
	public Boolean addLink2ArchiveColumn = false;
	public Boolean addLink2AdsColumn = false;
	public Boolean addSourcesInPublicationColumn = false;
	public String selectionHeaderTitle = null; // Selection Title if selection column is enabled. null = no text
	public Boolean blockRedraw = false; // block initial draw operation - restoreRedraw has to be called manually afterwards
    public Boolean isEsaskyData = true; // Data from user or not
    public Boolean addSelectionColumn = false;
    public Boolean addDatalinkLink2ArchiveColumn = false;
    public Integer selectable = null; // number of rows that can be selected simultaneously. null = no limit 
	public Boolean disableGoToColumn = false; // true to force remove goto column, even if there is a ra an dec column
	
	public String convertToJsonString() {
		JSONObject json = new JSONObject();
		json.put("addSendToVOApplicationColumn", JSONBoolean.getInstance(addSendToVOApplicationColumn));
		json.put("addLink2ArchiveColumn", JSONBoolean.getInstance(addLink2ArchiveColumn));
		json.put("addLink2AdsColumn", JSONBoolean.getInstance(addLink2AdsColumn));
		json.put("addSourcesInPublicationColumn", JSONBoolean.getInstance(addSourcesInPublicationColumn));
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
		return json.toString();
	}
}