package esac.archive.esasky.cl.web.client.view.resultspanel.column;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

import esac.archive.ammi.ifcs.model.descriptor.IDescriptor;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.model.FilterObserver;
import esac.archive.esasky.cl.web.client.model.TableElement;
import esac.archive.esasky.cl.web.client.model.TableRow;
import esac.archive.esasky.cl.web.client.view.resultspanel.RowsFilterObserver;
import esac.archive.esasky.cl.web.client.view.resultspanel.tab.filter.StringFilterDialogBox;

public class Link2ArchiveColumn extends SortableColumn<SafeHtml>{

	private IDescriptor descriptor;
	private final StringFilterDialogBox stringFilter;
	
	public Link2ArchiveColumn(String label, IDescriptor descriptor, String filterButtonId, RowsFilterObserver rowsFilterObserver){
		super(label, new SafeHtmlCell(), rowsFilterObserver);
		this.descriptor = descriptor;
		this.stringFilter = new StringFilterDialogBox(label, filterButtonId, new FilterObserver() {
			
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
	            
	            String archiveURL = buildArchiveURL(row, descriptor);
	            
	            SafeHtmlBuilder sb = new SafeHtmlBuilder();
	            sb.appendHtmlConstant("<a href='" + archiveURL
                        + "' onclick=\"trackOutboundLink('" + archiveURL
                        + "'); return false; \" target='_blank' >"
                        + element.getValue() + "</a>");
                return sb.toSafeHtml();
	        }
	    }
	    return null;
	}
	
	private String getOnlyElementValue(TableRow row) {
	    for (TableElement element : row.getElements()) {
	        if (label.equals(element.getLabel())) {
	        	return element.getValue();
	        }
	    }
	    return null;
	}
	
    private String buildArchiveURL(TableRow row, final IDescriptor descriptor) {
        String productURI = descriptor.getArchiveProductURI();
        RegExp regularExpression = RegExp.compile("@@@(.*?)@@@", "gm");

        for (MatchResult match = regularExpression.exec(descriptor.getArchiveProductURI()); match != null; match = regularExpression
                .exec(descriptor.getArchiveProductURI())) {
            String rowColumn = match.getGroup(1); // Group 1 is the match inside @s
            String label = TextMgr.getInstance().getText(rowColumn); //Gets translated label from tanslation_key
            String valueURI = row.getElementByLabel(label).getValue();
            productURI = productURI.replace("@@@" + rowColumn + "@@@", valueURI);
        }
        return descriptor.getArchiveURL() + productURI;
    }
    
	public static SafeHtml getLinkHtml(String value, String archiveURL, String archiveProductUrl) {
	    
	    String[] archiveProductURI = archiveProductUrl.split("@@@");

        StringBuilder finalURI = new StringBuilder(archiveURL);
        for (int i = 0; i < archiveProductURI.length; i++) {
            if (i % 2 == 0) {
                finalURI.append(archiveProductURI[i]);
            } else {
                finalURI.append(value);
            }
        }
        
	    SafeHtmlBuilder sb = new SafeHtmlBuilder();
	    
        sb.appendHtmlConstant("<a href='" + finalURI.toString()
                + "' onclick=\"trackOutboundLink('" + finalURI.toString()
                + "'); return false; \" target='_blank' >"
                + value + "</a>");
     
        return sb.toSafeHtml();
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
	protected int compare(String object1, String object2) {
		boolean validIntegerId = false;
		int id1 = 0;
		int id2 = 0;
		try {
			id1 = Integer.parseInt(object1);
			id2 = Integer.parseInt(object2);
			validIntegerId = true;
		} catch(NumberFormatException e) {
		}
		if(validIntegerId) {
			return id1 > id2 ? -1 : 1;
		} else {
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
	}

}
