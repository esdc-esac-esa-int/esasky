package esac.archive.esasky.cl.web.client.view.resultspanel.column;


import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

import esac.archive.esasky.cl.web.client.model.FilterObserver;
import esac.archive.esasky.cl.web.client.model.TableElement;
import esac.archive.esasky.cl.web.client.model.TableRow;
import esac.archive.esasky.cl.web.client.view.resultspanel.RowsFilterObserver;
import esac.archive.esasky.cl.web.client.view.resultspanel.tab.filter.FilterDialogBox;
import esac.archive.esasky.cl.web.client.view.resultspanel.tab.filter.StringFilterDialogBox;

public class LinkListColumn extends SortableColumn<SafeHtml>{

	private String splitByString;
	private String linkUrl;
	private String replaceString;
	private String showAllString;
	private int maxLinks;
	private final StringFilterDialogBox stringFilter;
	
	public LinkListColumn(String tapName, String label, String splitByString, String linkUrl, String replaceString, 
			String showAllString, int maxLinks, String filterButtonId, RowsFilterObserver rowsFilterObserver){
		super(tapName, label, new SafeHtmlCell(), rowsFilterObserver);
		this.splitByString = splitByString;
		this.linkUrl = linkUrl;
		this.replaceString = replaceString;
		this.showAllString = showAllString;
		this.maxLinks = maxLinks;
		this.stringFilter = new StringFilterDialogBox(tapName, label, filterButtonId, new FilterObserver() {
			
			@Override
			public void onNewFilter() {
				filter();
			}
		});
	}

	@Override
	public SafeHtml getValue(TableRow row) {
	    for (TableElement element : row.getElements()) {
	        if (label.equals(element.getLabel())) {
	            return getLinkList(element.getValue(), splitByString, linkUrl, replaceString, showAllString, maxLinks);
	        }
	    }
	    return null;
	}
	
	public void createFilter(Double min, Double max) {
		//DUMMY
	}
	
	public static SafeHtml getLinkList(String linkListValue, String splitByString, String linkUrl, String replaceString, String showAllString, int maxLinks) {
	    SafeHtmlBuilder sb = new SafeHtmlBuilder();

        String[] valueList = linkListValue.split(splitByString);
        String styleStr = "";
        int appendedLinks = 0;
        boolean showAllAppended = false;
        for (String value : valueList) {
            String finalURL = linkUrl.replace(replaceString, replaceLast(value.replaceAll("'", "%27").replaceAll(" ", "%20"), "%20", "%2C%20"));
            final boolean isLastLink = (appendedLinks == valueList.length -1);
            
                sb.appendHtmlConstant("<a href='" + finalURL
                                        + "' onclick=\"trackOutboundLink('" + finalURL
                                        + "'); event.stopPropagation(); return false; \" target='_blank' " + styleStr + ">"
                                        + value + ((!isLastLink) ? "," : "" ) + "</a>&nbsp;");
                
                if (appendedLinks > maxLinks && !showAllAppended && !isLastLink) {
                
                    sb.appendHtmlConstant("<a href='#' " 
                                    + "onclick=\"$(this).parent().find('a').fadeIn(); $(this).hide(); " 
                                    + "event.stopPropagation(); return false; \" >" 
                                        + showAllString + "</a>");
                    
                styleStr = "style=\"display: none;\" ";
                showAllAppended = true;
            }
            
            appendedLinks ++;
        }
        
        return sb.toSafeHtml();
	}
	
	private String getOnlyElementValue(TableRow row) {
	    for (TableElement element : row.getElements()) {
	        if (label.equals(element.getLabel())) {
	        	return element.getValue();
	        }
	    }
	    return null;
	}
	
	private static String replaceLast(String string, String find, String replace) {
        int lastIndex = string.lastIndexOf(find);
        
        if (lastIndex == -1) {
            return string;
        }
        
        String beginString = string.substring(0, lastIndex);
        String endString = string.substring(lastIndex + find.length());
        
        return beginString + replace + endString;
    }

	private boolean match(String value, String filter) {
		value = value.toLowerCase().trim();
		filter = filter.trim();
		if(!filter.contains("|")) {
			return value.contains(filter.toLowerCase());
		}
		String[] separateFilters = filter.split("\\|");
		for(String oneFilter : separateFilters) {
			if(value.contains(oneFilter.trim().toLowerCase())) {
				return true;
			} 
		}
		return false;
	}
	
	protected void filter() {
		String filter = stringFilter.getFilterString();
		Set<Integer> rowsIdsToRemove = new HashSet<Integer>();
		Set<Integer> rowsIdsToAdd = new HashSet<Integer>();
		
		Iterator<TableRow> iterator = originalRows.iterator();
		while (iterator.hasNext()) {
			TableRow row = iterator.next();
			if (match(getOnlyElementValue(row), filter) && removedRowIds.contains(row.getShapeId())) {  
				removedRowIds.remove(row.getShapeId());
				rowsIdsToAdd.add(row.getShapeId());
				setDirty();
			} else if (!match(getOnlyElementValue(row), filter) && !removedRowIds.contains(row.getShapeId())){
				rowsIdsToRemove.add(row.getShapeId());
				removedRowIds.add(row.getShapeId());
				setDirty();
			}
		}
		
		if(isTableDirty()) {
			notifyDirty(rowsIdsToRemove, rowsIdsToAdd);
		}
		
		String tapFilter = this.tapName  + " = \'" + filter + "\'";
		notifyFilterChanged(tapFilter);
	}
	
	@Override
	public void showFilter() {
		stringFilter.show();
	}
	
	@Override 
	public void ensureCorrectFilterButtonStyle() {
		stringFilter.ensureCorrectFilterButtonStyle();
	}
	
	@Override
	protected void applyFilterOnNewDataSet() {
		if(stringFilter.isFilterActive()) {
			filter();
		}
	}
	
	@Override
	protected int compare(String object1, String object2) {
		if (("".equals(object1) && "".equals(object2)) || (null == object1 && null == object2)) {
			return 0;
		}
		if ("".equals(object1) || null == object1) {
			return -1;
		}
		if ("".equals(object2) || null == object2) {
			return 1;
		}
		return object1.compareToIgnoreCase(object2);
	}

	@Override
	protected FilterDialogBox getFilterBox() {
		return stringFilter;
	}

}
