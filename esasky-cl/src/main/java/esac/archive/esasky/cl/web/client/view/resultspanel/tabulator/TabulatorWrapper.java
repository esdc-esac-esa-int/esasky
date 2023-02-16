package esac.archive.esasky.cl.web.client.view.resultspanel.tabulator;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Timer;
import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.event.IsShowingCoordintesInDegreesChangeEvent;
import esac.archive.esasky.cl.web.client.model.FilterObserver;
import esac.archive.esasky.cl.web.client.repository.MocRepository;
import esac.archive.esasky.cl.web.client.utility.AladinLiteWrapper;
import esac.archive.esasky.cl.web.client.utility.CoordinateUtils;
import esac.archive.esasky.cl.web.client.utility.EsaSkyWebConstants;
import esac.archive.esasky.cl.web.client.utility.ExtTapUtils;
import esac.archive.esasky.cl.web.client.view.animation.OpacityAnimation;
import esac.archive.esasky.cl.web.client.view.common.DropDownMenu;
import esac.archive.esasky.cl.web.client.view.common.MenuItem;
import esac.archive.esasky.cl.web.client.view.resultspanel.tab.filter.DateFilterDialogBox;
import esac.archive.esasky.cl.web.client.view.resultspanel.tab.filter.FilterDialogBox;
import esac.archive.esasky.cl.web.client.view.resultspanel.tab.filter.RangeFilterDialogBox;
import esac.archive.esasky.cl.web.client.view.resultspanel.tab.filter.ValueFormatter;
import esac.archive.esasky.ifcs.model.client.GeneralJavaScriptObject;
import esac.archive.esasky.ifcs.model.coordinatesutils.SkyViewPosition;
import esac.archive.esasky.ifcs.model.descriptor.CommonTapDescriptor;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class TabulatorWrapper {

    private TabulatorCallback tabulatorCallback;
    private GeneralJavaScriptObject tableJsObject;
    private GeneralJavaScriptObject abortController;
    private Map<String, FilterDialogBox> filterDialogs = new HashMap<>();
    private long lastHoverTime = 0;
    private int lastHoveredRow = -1;
    private String rowCountFooterId;
    private final Timer resultInformationAreaTimer;
    private final OpacityAnimation resultInformationAnimation;
    private boolean filtersShouldBeEnabled = true;
    private boolean waitingForMoc = false;
    private static final String ON_CHANGE = "onChange";

    public TabulatorWrapper(String divId, TabulatorCallback tabulatorCallback, TabulatorSettings settings) {
        this.tabulatorCallback = tabulatorCallback;
        tableJsObject = createColumnTabulator(this, divId, settings.convertToJsonString());
        abortController = createAbortController();
        CommonEventBus.getEventBus().addHandler(IsShowingCoordintesInDegreesChangeEvent.TYPE, () -> reformat(tableJsObject));

        rowCountFooterId = divId + "_rowCount";
        final Element rowCountFooter = Document.get().getElementById(rowCountFooterId);
        resultInformationAnimation = new OpacityAnimation(rowCountFooter);

        resultInformationAreaTimer = new Timer() {

            @Override
            public void run() {
                resultInformationAnimation.animateTo(1, 500);
                rowCountFooter.getStyle().setProperty("pointerEvents", "auto");
                cancel();
            }
        };
    }

    public void setAddHipsColumn(boolean addHipsColumn) {
        tableJsObject.setProperty("addHipsColumn", addHipsColumn);
    }

    public void onRowCountFooterMouseOver() {
        Element rowCountFooter = Document.get().getElementById(rowCountFooterId);
        resultInformationAnimation.animateTo(0, 500);
        resultInformationAreaTimer.schedule(4000);
        rowCountFooter.getStyle().setProperty("pointerEvents", "none");
    }

    public boolean isSelected(int rowId) {
        GeneralJavaScriptObject row = tableJsObject.invokeFunction("getRow", "" + rowId);
        return GeneralJavaScriptObject.convertToBoolean(row.invokeFunction("isSelected"));
    }

    public void selectRow(int rowId) {
        selectRow(rowId, true);
    }

    public void selectRow(int rowId, boolean scrollTo) {
        GeneralJavaScriptObject row = tableJsObject.invokeFunction("getRow", "" + rowId);
        row.invokeFunction("select");
        if (scrollTo) {
            row.invokeFunction("scrollTo");
        }
    }

    public void selectRows(int[] rowIds) {
        String json = "[";
        for (int rowId : rowIds) {
            json += rowId;
            json += ",";
        }
        json = json.substring(0, json.length() - 1);
        json += "]";
        GeneralJavaScriptObject rows = GeneralJavaScriptObject.createJsonObject(json);
        selectRows(rows);
    }

    public void deselectRows(int[] rowIds) {
        String json = "[";
        for (int rowId : rowIds) {
            json += rowId;
            json += ",";
        }
        json = json.substring(0, json.length() - 1);
        json += "]";
        GeneralJavaScriptObject rows = GeneralJavaScriptObject.createJsonObject(json);
        deselectRows(rows);
    }

    public void selectRows(GeneralJavaScriptObject rowIdArray) {
        selectRows(tableJsObject, rowIdArray);
    }

    private native void selectRows(GeneralJavaScriptObject tableJsObject, GeneralJavaScriptObject rowIdArray)/*-{
        tableJsObject.selectRow(rowIdArray);
    }-*/;

    public void deselectRows(GeneralJavaScriptObject rowIdArray) {
        deselectRows(tableJsObject, rowIdArray);
    }

    private native void deselectRows(GeneralJavaScriptObject tableJsObject, GeneralJavaScriptObject rowIdArray)/*-{
        tableJsObject.deselectRow(rowIdArray);
    }-*/;

    public int getTableHeight() {
        return getTableHeight(tableJsObject);
    }

    private native int getTableHeight(GeneralJavaScriptObject tableJsObject)/*-{
        return tableJsObject.rowManager.height;
    }-*/;

    public void deselectRow(int rowId) {
        GeneralJavaScriptObject row = tableJsObject.invokeFunction("getRow", "" + rowId);
        row.invokeFunction("deselect");
    }

    public void deselectAllRows() {
        tableJsObject.invokeFunction("deselectRow");
    }

    public void hoverStart(int rowId) {
        GeneralJavaScriptObject element = tableJsObject.invokeFunction("getRow", "" + rowId).invokeFunction("getElement").getProperty("style");
        element.setProperty("background-color", "rgba(255, 255, 255, 0.15)");
    }

    public void hoverStop(int rowId) {
        GeneralJavaScriptObject element = tableJsObject.invokeFunction("getRow", "" + rowId).invokeFunction("getElement").getProperty("style");
        element.setProperty("background-color", "");
    }

    private native void redraw(GeneralJavaScriptObject tableJsObject)/*-{
        tableJsObject.redraw(true);
    }-*/;

    private native void reformat(GeneralJavaScriptObject tableJsObject)/*-{
        tableJsObject.rowManager.rows.forEach(function (row) {
            row.reinitialize()
        });
    }-*/;

    public void showColumn(String field) {
        showColumn(tableJsObject, field);
    }

    private native void showColumn(GeneralJavaScriptObject tableJsObject, String field)/*-{
        tableJsObject.getColumn(field).show(true);
    }-*/;

    public void hideColumn(String field) {
        hideColumn(tableJsObject, field);
    }

    private native void hideColumn(GeneralJavaScriptObject tableJsObject, String field)/*-{
        var column = tableJsObject.getColumn(field);
        if (column  ) {
            column.hide(true);
        }
    }-*/;

    public void sortByColumn(String column, boolean asc) {
        sortByColumn(tableJsObject, column, asc ? "asc" : "desc");
    }

    private native void sortByColumn(GeneralJavaScriptObject tableJsObject, String column, String direction) /*-{
        tableJsObject.setSort(column, direction);
    }-*/;

    public void downloadCsv(String fileName) {
        downloadCsv(tableJsObject, fileName);
    }

    private native void downloadCsv(GeneralJavaScriptObject tableJsObject, String fileName)/*-{
        tableJsObject.download(tableJsObject.csvTableFormatter, fileName, {}, tableJsObject.getSelectedData().length > 0 ? "selected" : "active");
    }-*/;

    public void downloadVot(String fileName, String resourceName) {
        downloadVot(tableJsObject, fileName, resourceName);
    }

    private native void downloadVot(GeneralJavaScriptObject tableJsObject, String fileName, String resourceName)/*-{
        tableJsObject.download(tableJsObject.voTableFormatter, fileName, {resourceName: resourceName}, tableJsObject.getSelectedData().length > 0 ? "selected" : "active");
    }-*/;

    public String getVot(String resourceName) {
        return getVot(tableJsObject, resourceName);
    }

    private native String getVot(GeneralJavaScriptObject tableJsObject, String resourceName)/*-{
        return tableJsObject.getVoTableString(tableJsObject.getSelectedRows().length > 0
            ? tableJsObject.modules.download.generateExportList("selected") : tableJsObject.modules.download.generateExportList("active"), resourceName);
    }-*/;

    public String exportTableAsJson() {
        return exportTableAsJson(true);
    }

    public String exportTableAsJson(boolean applyFilters) {
        return exportTableAsJson(tableJsObject, applyFilters);
    }

    private native String exportTableAsJson(GeneralJavaScriptObject tableJsObject, boolean applyFilters)/*-{
        var json = "";
        if (applyFilters) {
            tableJsObject.modules.download.download("json", "json.json", {}, tableJsObject.getSelectedRows().length > 0
                ? "selected" : "active", function (data) {
                json = data;
                return false;
            });
        } else {
            tableJsObject.modules.download.download("json", "json.json", {}, "all", function (data) {
                json = data;
                return false;
            });
        }
        return json;
    }-*/;

    public GeneralJavaScriptObject[] getSelectedRows() {
        return getSelectedRows(tableJsObject);
    }

    private native GeneralJavaScriptObject[] getSelectedRows(GeneralJavaScriptObject tableJsObject)/*-{
        var filteredData = tableJsObject.getData(true);
        var selectedData = tableJsObject.getSelectedData();

        function isRowSelected(row){
            function hasIdenticalId(selectedRow) {
                return selectedRow.id === row.id;
            }
            return selectedData.find(hasIdenticalId);
        }
        return filteredData.filter(isRowSelected);
    }-*/;

    public GeneralJavaScriptObject[] getAllRows() {
        return getAllRows(tableJsObject);
    }

    private native GeneralJavaScriptObject[] getAllRows(GeneralJavaScriptObject tableJsObject)/*-{
        return tableJsObject.getData();
    }-*/;

    public int getVisibleRowCount() {
        return getVisibleRowCount(tableJsObject);
    }

    private native int getVisibleRowCount(GeneralJavaScriptObject tableJsObject)/*-{
        return tableJsObject.getDataCount("active");
    }-*/;

    public GeneralJavaScriptObject[] getColumns() {
        return getColumns(tableJsObject);
    }

    private native GeneralJavaScriptObject[] getColumns(GeneralJavaScriptObject tableJsObject)/*-{
        return tableJsObject.getColumns();
    }-*/;

    public GeneralJavaScriptObject[] getColumnDefinitions() {
        return getColumnDefinitions(tableJsObject);
    }

    private native GeneralJavaScriptObject[] getColumnDefinitions(GeneralJavaScriptObject tableJsObject)/*-{
        return tableJsObject.getColumnDefinitions();
    }-*/;

    public GeneralJavaScriptObject[] getColumnLayout() {
        return getColumnLayout(tableJsObject);
    }

    private native GeneralJavaScriptObject[] getColumnLayout(GeneralJavaScriptObject tableJsObject)/*-{
        return tableJsObject.getColumnLayout();
    }-*/;

    public void blockRedraw() {
        blockRedraw(tableJsObject);
    }

    private native void blockRedraw(GeneralJavaScriptObject tableJsObject)/*-{
        tableJsObject.blockRedraw();
    }-*/;

    public void restoreRedraw() {
        restoreRedraw(tableJsObject);
    }

    private native void restoreRedraw(GeneralJavaScriptObject tableJsObject)/*-{
        tableJsObject.restoreRedraw();
    }-*/;

    public void redrawAndReinitializeHozVDom() {
        redrawAndReinitializeHozVDom(tableJsObject);
    }

    private native void redrawAndReinitializeHozVDom(GeneralJavaScriptObject tableJsObject)/*-{
        tableJsObject.vdomHoz.reinitialize(undefined, true);
        tableJsObject.redraw(true);
        tableJsObject.vdomHoz.reinitialize();
    }-*/;

    public void filterOnFov(String raCol, String decCol) {
        SkyViewPosition pos = CoordinateUtils.getCenterCoordinateInJ2000();
        double minRa;
        double maxRa;

        double ra = pos.getCoordinate().getRa();
        double dec = pos.getCoordinate().getDec();
        double fov = pos.getFov() / 2.0;
        double minDec = dec - fov;
        double maxDec = dec + fov;

        if (dec + fov > 90.0) {
            //Around north pole
            minDec = pos.getCoordinate().getDec() - fov;
        } else if (dec - pos.getFov() < -90.0) {
            //Around south pole
            maxDec = pos.getCoordinate().getDec() + fov;
        }

        // To handle ra fov closer to the poles
        fov = Math.abs(fov / Math.cos(dec * Math.PI / 180.0));

        String filterString = "";
        minRa = ra - fov;
        maxRa = ra + fov;
        if (minRa < 0) {
            minRa += 360;
            filterString += minRa + "," + 360;
            filterString += "," + 0 + "," + (maxRa % 360);
        } else if (maxRa > 360) {
            maxRa = maxRa % 360;
            filterString += 0 + "," + maxRa;
            filterString += "," + minRa + "," + 360;
        } else {
            filterString += minRa + "," + maxRa;
        }

        tableJsObject.setProperty("filteredOnFov", true);
        groupByFov(tableJsObject, raCol, filterString, decCol, minDec, maxDec);
    }

    public void groupByColumns(String... columnNames) {
        groupByColumns(tableJsObject, columnNames);
    }

    public native void groupByColumns(GeneralJavaScriptObject tableJsObject, String... columnNames) /*-{
        tableJsObject.setGroupBy(columnNames);
    }-*/;


    public native void groupByFov(GeneralJavaScriptObject tableJsObject, String raColumn, String filterString,
                                  String decColumn, double minDec, double maxDec)/*-{

        var split = filterString.split(",");

        var isWithin = function (data, column, index) {
            var startTrue = true;
            var endTrue = true;
            if (split[index].length > 0 && data[column] < parseFloat(split[index])) {
                startTrue = false;
            }
            if (split[index + 1].length > 0 && data[column] > parseFloat(split[index + 1])) {
                endTrue = false;
            }
            if (startTrue && endTrue) {
                return true;
            }

            if (split.length > index + 2) {
                return isWithin(data, column, index + 2);
            }
            return false;
        }
        var isInFov = function (data) {
            if (isWithin(data, raColumn, 0)
                && data[decColumn] >= minDec && data[decColumn] <= maxDec) {
                return "In Field of View";
            } else {
                return "Outside Field of View"
            }
        }


        tableJsObject.setGroupBy(isInFov);
    }-*/;

    public void clearGroups() {
        clearGroups(tableJsObject);
    }

    private native void clearGroups(GeneralJavaScriptObject tableJsObject) /*-{
        tableJsObject.setGroupBy();
    }-*/;

    public void filter(String column, String comparison, String value) {
        this.filter(tableJsObject, column, comparison, value);
    }

    public native void filter(GeneralJavaScriptObject tableJsObject, String column, String comparison, String value)/*-{
        tableJsObject.setFilter(column, comparison, value);
    }-*/;

    public void setPlaceholderText(String text) {
        setPlaceholderText(tableJsObject, text);
    }

    public native void setPlaceholderText(GeneralJavaScriptObject tableJsObject, String text)/*-{
        var div = $doc.createElement('div')
        div.innerText = text;
        div.className = "tabulator_emptyTable";
        tableJsObject.options.placeholder.innerText = "";
        tableJsObject.options.placeholder.appendChild(div);
    }-*/;


    public void onDataFiltered(String indexes) {
        List<Integer> indexArray = new LinkedList<Integer>();
        for (String s : indexes.split(",")) {
            if (s.length() > 0 && !s.equals("undefined")) {
                indexArray.add(Integer.parseInt(s));
            }
        }
        tabulatorCallback.onDataFiltered(indexArray);
    }

    public void createNumericFilterDialog(String tapName, String title, String filterButtonId, final GeneralJavaScriptObject onChangeFunc,
                                          final GeneralJavaScriptObject formatter, GeneralJavaScriptObject formatterParamsIfExisting) {

        final GeneralJavaScriptObject formatterParams = verifyFormatterParams(formatterParamsIfExisting);
        final ValueFormatter valueFormatter = new ValueFormatter() {

            @Override
            public double getValueFromFormat(String formattedValue) {
                formatterParams.setProperty("convertBack", true);
                double value = GeneralJavaScriptObject.convertToDouble(formatter.invokeSelf(createPretendCell(formattedValue), formatterParams));
                formatterParams.setProperty("convertBack", false);
                return value;
            }

            @Override
            public String formatValue(double value) {
                formatterParams.setProperty("convertBack", false);
                return GeneralJavaScriptObject.convertToString(formatter.invokeSelf(createPretendCell(value), formatterParams));
            }
        };

        FilterObserver filterObserver = filter -> onChangeFunc.invokeFunction(ON_CHANGE, filter);

        RangeFilterDialogBox filterDialog = new RangeFilterDialogBox(tapName, title, valueFormatter, filterButtonId, filterObserver);
        filterDialogs.put(tapName, filterDialog);
    }


    public void toggleNumericFilterDialog(String tapName, double minVal, double maxVal) {

        RangeFilterDialogBox filterDialogBox = (RangeFilterDialogBox) filterDialogs.get(tapName);

        filterDialogBox.setRange(minVal, maxVal, 2);
        filterDialogBox.toggle();
    }

    private native GeneralJavaScriptObject verifyFormatterParams(GeneralJavaScriptObject formatterParams)/*-{
        return formatterParams || {};
    }-*/;

    private native GeneralJavaScriptObject createPretendCell(Object value)/*-{
        return {
            getValue: function () {
                return value
            }
        };
    }-*/;

    public void createDateFilterDialog(String tapName, String title, String filterButtonId, final GeneralJavaScriptObject onChangeFunc) {
        FilterObserver filterObserver = new FilterObserver() {

            @Override
            public void onNewFilter(String filter) {
                onChangeFunc.invokeFunction(ON_CHANGE, filter);

            }
        };

        DateFilterDialogBox filterDialog = new DateFilterDialogBox(tapName, title, filterButtonId, filterObserver);

        filterDialogs.put(tapName, filterDialog);
    }

    public void toggleDateFilterDialog(String tapName, String minVal, String maxVal) {

        DateFilterDialogBox filterDialogBox = (DateFilterDialogBox) filterDialogs.get(tapName);
        filterDialogBox.setStartRange(minVal, maxVal);
        filterDialogBox.toggle();

    }

    public void showListFilterDialog(String tapName, String title, String filterButtonId, String list, final GeneralJavaScriptObject onChangeFunc) {

        if (!filterDialogs.containsKey(tapName)) {

            final DropDownMenu<String> dropDownMenu = new DropDownMenu<String>("", "", 125, filterButtonId + "_DropDownMenu");

            for (String item : list.split(",")) {
                MenuItem<String> dropdownItem = new MenuItem<>(item, item, item, true);
                dropDownMenu.addMenuItem(dropdownItem);
            }

            dropDownMenu.registerObserver(() -> {
                String object = dropDownMenu.getSelectedObject();
                onChangeFunc.invokeFunction(ON_CHANGE, object);
            });

            dropDownMenu.toggleMenuBar();
        }
    }

    public void goToCoordinateOfFirstRow() {
        tabulatorCallback.onCenterClicked(tableJsObject.invokeFunction("getRow", "0").invokeFunction("getData"));
    }

    public void insertData(GeneralJavaScriptObject data, GeneralJavaScriptObject metadata) {
        setMetadata(tableJsObject, metadata);
        setData(tableJsObject, abortController, data);
    }

    public void insertData(String data, GeneralJavaScriptObject metadata) {
        setMetadata(tableJsObject, metadata);
        setData(tableJsObject, abortController, data);
    }

    public void insertExternalTapData(GeneralJavaScriptObject data, GeneralJavaScriptObject metadata) {
        GeneralJavaScriptObject formattedMetadata = ExtTapUtils.formatExternalTapMetadata(metadata);
        GeneralJavaScriptObject formattedData = ExtTapUtils.formatExternalTapData(data, formattedMetadata);
        setMetadata(tableJsObject, formattedMetadata);
        setData(tableJsObject, abortController, formattedData);
    }

    private native String setMetadata(GeneralJavaScriptObject tableJsObject, GeneralJavaScriptObject metadata)/*-{
        return tableJsObject.metadata = metadata;
    }-*/;

    public void insertUserData(GeneralJavaScriptObject data) {
        setIsUserDataBool(tableJsObject);
        setData(convertDataToTabulatorFormat(tableJsObject, data, AladinLiteWrapper.getCoordinatesFrame().getValue()));

    }

    public void insertUserHeader(GeneralJavaScriptObject data) {
        setIsUserDataBool(tableJsObject);
        setData(tableJsObject, abortController, convertDataToHeaderFormat(tableJsObject, data));
    }

    private native void setIsUserDataBool(GeneralJavaScriptObject tableJsObject)/*-{
        tableJsObject.isEsaskyData = false;
    }-*/;

    private native String convertDataToTabulatorFormat(GeneralJavaScriptObject tableJsObject, GeneralJavaScriptObject data, String aladinFrame)/*-{
        return tableJsObject.convertDataToTabulatorFormat(data, aladinFrame);
    }-*/;

    private native String convertDataToHeaderFormat(GeneralJavaScriptObject tableJsObject, GeneralJavaScriptObject data)/*-{
        return tableJsObject.convertDataToHeaderFormat(data);
    }-*/;

    public void setData(String dataOrUrl) {
        setData(tableJsObject, abortController, dataOrUrl);
    }

    public void clearTable() {
        clearTable(tableJsObject);
        for (String key : filterDialogs.keySet()) {
            onFilterChanged(key, "");
        }
        filterDialogs.clear();
        lastHoveredRow = -1;
    }

    private native void clearTable(GeneralJavaScriptObject tableJsObject)/*-{
        tableJsObject.clearing = true;
        tableJsObject.dataLoaded = false;
        tableJsObject.showCount = false;
        tableJsObject.clearData();
        tableJsObject.filterData = [];
        tableJsObject.metadata = [];
        tableJsObject.clearHeaderFilter();
        previouslySelectedMap = [];
        tableJsObject.dataLoaded = false;
        tableJsObject.clearing = false;
    }-*/;

    private native void setData(GeneralJavaScriptObject tableJsObject, GeneralJavaScriptObject abortController, Object dataOrUrl)/*-{
        tableJsObject.dataLoaded = false;
        tableJsObject.setData(dataOrUrl, {}, {signal: abortController.signal});

        var observer = new MutationObserver(function (mutations) {
            for (var i = 0; i < mutations.length; i++) {
                for (var j = 0; j < mutations[i].addedNodes.length; j++) {
                    if (mutations[i].addedNodes[j].classList &&
                        (mutations[i].addedNodes[j].classList.contains("tabulator-cell") ||
                            mutations[i].addedNodes[j].getAttribute('role') == 'Header')) {
                        tableJsObject.vdomHoz.reinitialize(undefined, true);
                        tableJsObject.redraw(true);
                        tableJsObject.vdomHoz.reinitialize();

                        this.disconnect();
                        return;
                    }
                }
            }
        });

        observer.observe(tableJsObject.element, {
            childList: true,
            subtree: true
        });
    }-*/;

    public void setQueryMode() {
        setQueryMode(this, tableJsObject);
    }

    private native void setQueryMode(TabulatorWrapper wrapper, GeneralJavaScriptObject tableJsObject)/*-{
        tableJsObject.options.ajaxResponse = function (url, params, response) {
            wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::onAjaxResponse()();
            var metadata = response.metadata == null ? response.columns : response.metadata;

            var raColumnName = wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::getRaColumnName()();
            var decColumnName = wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::getDecColumnName()();

            var sortedMetadata = [];
            var indexesMoved = [];
            for (var j = 0; j < metadata.length; j++) {
                if (metadata[j].name === "id") {
                    metadata[j].name = 'identifier';
                } else if (metadata[j].name === raColumnName) {
                    metadata[j].datatype = "RA";
                } else if (metadata[j].name === decColumnName) {
                    metadata[j].datatype = "DEC";
                }

                var tableName = wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::getTableName()();
                var textKey = tableName + "_" + metadata[j].name;
                if ($wnd.esasky.hasInternationalizationText(textKey)) {
                    metadata[j].displayName = $wnd.esasky.getDefaultLanguageText(textKey);
                } else {
                    metadata[j].displayName = $wnd.esasky.getColumnDisplayText(metadata[j].name);

                    // Only add DB units to the column header if we don't supply our own through internationalization.
                    var unit = wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::getColumnUnit(*)(metadata[j].name);
                    if (unit) {
                        metadata[j].displayName += " <i>[" + unit + "]</i>";
                    }
                }

                metadata[j].visible = wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::isColumnVisible(*)(metadata[j].name, j);
            }

            sortedMetadata = sortedMetadata.filter(function (element) {
                return element !== undefined;
            });

            var needsFormatting = function (data) {
                return Array.isArray(data[0])
            }
            var data = needsFormatting(response.data) ? @esac.archive.esasky.cl.web.client.utility.ExtTapUtils::formatExternalTapData(*)(response.data, metadata) : response.data;


            // Tabulator has reserved the ID column
            for (var i = 0; i < data.length; i++) {
                data[i]["id"] = i;
            }

            var index = indexesMoved.pop();
            while (index !== undefined) {
                metadata.splice(index, 1);
                index = indexesMoved.pop();
            }
            metadata = sortedMetadata.concat(metadata);

            tableJsObject.metadata = metadata;
            tableJsObject.filterData = []
            tableJsObject.columnDef = [];
            tableJsObject.showCount = true;
            tableJsObject.dataLoaded = true;
            return data;
        }
    }-*/;

    public void show() {
        if (isMOCMode()) { //In defaultQueryMode MutationObserver redraws, if necessary
            Scheduler.get().scheduleFinally(() -> redraw(tableJsObject));
        }
    }

    private native GeneralJavaScriptObject createAbortController() /*-{
        return new AbortController();
    }-*/;


    public native String[] getNonDatabaseColumns() /*-{
        return $wnd.esasky.nonDatabaseColumns;
    }-*/;



    private void setTableErrorMessage(String error, String details) {
        setTableErrorMessage(tableJsObject, error, details);
    }

    private native void setTableErrorMessage(GeneralJavaScriptObject tableJsObject, String error, String details) /*-{
        var template = document.createElement('template');

        if (details != null && details.length > 0) {
            template.innerHTML = "<div>" + $wnd.esasky.getInternationalizationText("tabulator_loadFailed")
                + ".<br><br>" + error.trim()
                + "<br><br> "
                + "<details style=\"color: grey; opacity: 60%;border: 1px solid grey;border-radius: 4px;padding: 0.5em; overflow: scroll; max-height: 80px;font-size: small;\">"
                + "<p style=\"white-space: pre-line; text-align: left; font-size: smaller;\">" + details + "</p>"
                + "</details>"
                + "</div>";
        } else {
            template.innerHTML = "<div>" + $wnd.esasky.getInternationalizationText("tabulator_loadFailed")
                + ".<br><br>" + error.trim() +  "</div>";
        }

        tableJsObject.modules.ajax.errorElement = template.content.firstChild;
        tableJsObject.modules.ajax.showError();
    }-*/;

    private native GeneralJavaScriptObject createColumnTabulator(TabulatorWrapper wrapper, String divId, String settingsString) /*-{
        var settings = JSON.parse(settingsString);
        if (settings.selectable == null) {
            settings.selectable = true;
        }

        $wnd.esasky.nonDatabaseColumns = ["rowSelection", "centre", "link2archive", "addLink2AdsColumn", "samp", "sourcesInPublication"];

        var footerCounter = "<div></div><div id=\"" + divId + "_rowCount\" class=\"footerCounter\">0</div>"
        var table = new $wnd.Tabulator("#" + divId, {
            height: "100%", // set height of table (in CSS or here), this enables the Virtual DOM and improves render speed dramatically (can be any valid css height value)
            placeholder: "",
            footerElement: footerCounter,
            virtualDomHoz: true,
            groupHeader: wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::getGroupHeaderFunc()(),
            groupStartOpen: wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::getGroupStartOpenFunc()(),
            groupToggleElement: "header",
            dataFiltered: wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::getDataFilteredFunc(*)(wrapper),
            dataLoaded: wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::getDataLoadedFunc(*)(wrapper, settings),
            dataLoading: wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::getDataLoadingFunc(*)(wrapper, divId, settings),
            selectable: settings.selectable,
            ajaxError: function (error) {
                var test = settings;
                if (settings.showDetailedErrors) {
                    var mainError = "";
                    if (error.status >= 500) {
                        mainError = $wnd.esasky.getInternationalizationText("tabulator_loadFailed_500");
                    } else if (settings.fovLimitDisabled === false){
                        mainError = $wnd.esasky.getInternationalizationText("tabulator_loadFailed_400");
                    }

                    if (error.text) {
                        error.text().then(function(text){
                            wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::setTableErrorMessage(Ljava/lang/String;Ljava/lang/String;)(mainError, text);
                        });
                    } else {
                        wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::setTableErrorMessage(Ljava/lang/String;Ljava/lang/String;)(mainError, null);
                    }
                }

                wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::onAjaxResponseError(Ljava/lang/String;)(error.message);
            },
            ajaxLoaderLoading: @esac.archive.esasky.cl.web.client.view.common.LoadingSpinner::getLoadingSpinner()(),
            ajaxLoaderError: $wnd.esasky.getInternationalizationText("tabulator_loadFailed"),
            rowSelectionChanged: wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::getRowSelectionChangedFunc(*)(wrapper),

            rowMouseEnter: function (e, row) {
                wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::onRowEnter(I)(row.getIndex());
            },

            movableColumns: true,
            autoColumns: true,
            layout: settings.tableLayout
        });

        if (settings.blockRedraw) {
            table.blockRedraw();
        }


        table.isEsaskyData = settings.isEsaskyData;
        //Remove the clearSelection function to make sure that it is possible to select and copy text from the table
        table._clearSelection = function () {
        };

        table.rowManager.adjustTableSize = function () {
            //Adapted from Tabulator 4.6.2. Change to remove that it changes with the footer height
            var initialHeight = this.element.clientHeight,
                modExists;

            if (this.renderMode === "virtual") {
                var otherHeight = this.columnManager.getElement().offsetHeight;

                if (this.fixedHeight) {
                    this.element.style.minHeight = "calc(100% - " + otherHeight + "px)";
                    this.element.style.height = "calc(100% - " + otherHeight + "px)";
                    this.element.style.maxHeight = "calc(100% - " + otherHeight + "px)";
                } else {
                    this.element.style.height = "";
                    this.element.style.height = this.table.element.clientHeight - otherHeight + "px";
                    this.element.scrollTop = this.scrollTop;
                }
                var heightChanged = this.height != this.element.clientHeight;
                this.height = this.element.clientHeight;
                if (heightChanged) {
                    wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::onTableHeightChanged()();
                }
                this.vDomWindowBuffer = this.table.options.virtualDomBuffer || this.height;
                if (this.table.showCount) {
                    footerOffset = (this.table.footerManager && !this.table.footerManager.external ? this.table.footerManager.getElement().offsetHeight : 0);
                } else {
                    footerOffset = 0;
                }
                this.table.footerManager.element.style.marginTop = -footerOffset + "px";

                //check if the table has changed size when dealing with variable height tables
                if (!this.fixedHeight && initialHeight != this.element.clientHeight) {
                    modExists = this.table.modExists("resizeTable");

                    if (modExists && !this.table.modules.resizeTable.autoResize || !modExists) {
                        this.redraw();
                        this.vdomHoz.reinitialize();
                    }
                }
            }
        };

        table.getVoTableString = function (list, resourceName) {
            // Add VOT XML Schema
            var votData = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
            votData += "<VOTABLE version=\"1.3\" xmlns=\"//www.ivoa.net/xml/VOTable/v1.3\">\n";
            votData += "<RESOURCE name=\"" + $wnd.esasky.escapeXml(resourceName) + "\">\n";
            votData += "<TABLE>\n";

            var esaskyToVOStandardType = {};
            esaskyToVOStandardType["DOUBLE"] = "double"
            esaskyToVOStandardType["INTEGER"] = "int"
            esaskyToVOStandardType["SHORT"] = "short"
            esaskyToVOStandardType["BIGINT"] = "long";
            esaskyToVOStandardType["STRING"] = "char";
            esaskyToVOStandardType["VARCHAR"] = "char";
            esaskyToVOStandardType["CHAR"] = "char";
            esaskyToVOStandardType["REAL"] = "float";
            esaskyToVOStandardType["FLOAT"] = "float";
            esaskyToVOStandardType["SMALLINT"] = "int";
            esaskyToVOStandardType["TIMESTAMP"] = "char";
            esaskyToVOStandardType["BOOLEAN"] = "boolean";

            // Adds headers to xml
            table.metadata.forEach(function (columnInfo) {
                if (columnInfo.name !== "sso_name_splitter" && table.getColumn(columnInfo.name).getDefinition().download) {
                    votData += "<FIELD";
                    Object.keys(columnInfo).forEach(function (key) {
                        var value = columnInfo[key];
                        if (value !== null) {
                            if (key == "datatype") {
                                value = esaskyToVOStandardType[value.toUpperCase()];
                                if (value == "char" && !columnInfo.hasOwnProperty("arraysize")) {
                                    votData += " arraysize =\"*\""
                                }
                            }
                            votData += " " + key + "=\"" + $wnd.esasky.escapeXml(value) + "\"";
                        }

                    });
                    votData += "/>\n";
                }
            });

            // Adds data to xml
            votData += "<DATA>\n";
            votData += "<TABLEDATA>\n";

            list.forEach(function (row) {
                switch (row.type) {
                    case "row":
                        votData += "<TR>\n";
                        table.metadata.forEach(function (columnInfo) {
                            if (columnInfo.name !== "sso_name_splitter" && table.getColumn(columnInfo.name).getDefinition().download) {
                                var value = "";
                                row.columns.some(function (column) {
                                    if (column.component.getField() == columnInfo.name) {
                                        value = column.value;
                                        return true;
                                    }
                                });
                                votData += "<TD>"
                                    + $wnd.esasky.escapeXml(value)
                                    + "</TD>\n";
                            }
                        });

                        votData += "</TR>\n";
                        break;
                }
            })

            votData += "</TABLEDATA>\n";
            votData += "</DATA>\n";
            votData += "</TABLE>\n";
            votData += "</RESOURCE>\n";
            votData += "</VOTABLE>\n";

            return votData;
        }
        table.voTableFormatter = function (list, options, setFileContents) {
            setFileContents(table.getVoTableString(list, options.resourceName), "application/x-votable+xml");
        }

        table.csvTableFormatter = function csv(list, options, setFileContents) {
            var delimiter = options && options.delimiter ? options.delimiter : ",",
                fileContents = [],
                headers = [];

            list.forEach(function (row) {
                var item = [];

                switch (row.type) {
                    case "group":
                        console.warn("Download Warning - CSV downloader cannot process row groups");
                        break;

                    case "calc":
                        console.warn("Download Warning - CSV downloader cannot process column calculations");
                        break;

                    case "header":
                        row.columns.forEach(function (col, i) {
                            if (col && col.depth === 1 && !$wnd.esasky.nonDatabaseColumns.includes(col.component.getField())) {
                                headers.push(typeof col.value == "undefined" || typeof col.value == "null" ? "" : col.value);
                            }
                        });


                        break;

                    case "row":
                        row.columns.forEach(function (col) {

                            if (col && !$wnd.esasky.nonDatabaseColumns.includes(col.component.getField())) {

                                switch (typeof col.value) {
                                    case "object":
                                        col.value = JSON.stringify(col.value);
                                        break;

                                    case "undefined":
                                    case "null":
                                        col.value = "";
                                        break;
                                }

                                item.push('"' + String(col.value).split('"').join('""') + '"');
                            }
                        });

                        fileContents.push(item.join(delimiter));
                        break;
                }
            });

            if (headers.length) {
                fileContents = [headers].concat(fileContents);
            }

            fileContents = fileContents.join("\n");

            if (options.bom) {
                fileContents = "\uFEFF" + fileContents;
            }

            setFileContents(fileContents, "text/csv");
        }

        table.convertDataToHeaderFormat = function (userData) {
            var newMeta = [];
            var filterData = {};

            var descMetaData = wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::getDescriptorMetaData()();
            var data = userData.metadata
            newMeta = new Array(data.length);
            for (var i = 0; i < data.length; i++) {
                var col = data[i]
                var colName = col.name
                var val = col.value
                var metaName = colName.substring(0, colName.length - 4)
                var datatype = col.datatype;
                var label = colName;
                var ucd = col.ucd ? col.ucd : "";

                //If not in descMetaData add to unique spot in end and then we remove all empty slots in end
                var metaDataIndex = data.length + newMeta.length;
                var visible = false;

                var metaObj = (function (name) {
                    return descMetaData.find(function (x) {
                        if (x.name === name) {
                            return x;
                        }
                    });
                })(metaName);

                if (metaObj) {
                    label = metaObj.name;
                    visible = metaObj.principal;
                    if (metaObj.datatype !== null) {
                        datatype = metaObj.datatype.toUpperCase();
                    }
                }


                var tableName = wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::getTableName()();
                var textKey = tableName + "_" + label;
                var displayName = "";
                if ($wnd.esasky.hasInternationalizationText(textKey)) {
                    displayName = $wnd.esasky.getDefaultLanguageText(textKey);
                } else {
                    displayName = $wnd.esasky.getColumnDisplayText(label);

                    // Only add DB units to the column header if we don't supply our own through internationalization.
                    var unit = wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::getColumnUnit(*)(label);
                    if (unit) {
                        displayName += " <i>[" + unit + "]</i>";
                    }
                }

                if (!filterData.hasOwnProperty(metaName)) {
                    filterData[metaName] = {};
                }

                if (colName.endsWith("_min")) {
                    if (datatype === "TIMESTAMP" || datatype === "DATETIME" || ucd.includes("time.start") || ucd.includes("time.end") || col.unit === "time") {
                        filterData[metaName]["min"] = val;
                    } else {
                        filterData[metaName]["min"] = parseFloat(val);
                    }
                    meta = {
                        name: metaName, displayName: displayName, datatype: datatype, visible: visible,
                        description: col.description, ucd: col.ucd, utype: col.utype, unit: col.unit
                    }
                    newMeta.splice(metaDataIndex, 1, meta)

                } else if (colName.endsWith("_max")) {

                    if (datatype === "TIMESTAMP" || datatype === "DATETIME" || ucd.includes("time.start") || ucd.includes("time.end") || col.unit === "time") {
                        filterData[metaName]["max"] = val;

                    } else {
                        filterData[metaName]["max"] = parseFloat(val);
                    }

                } else {
                    meta = {
                        name: metaName, displayName: displayName, datatype: datatype, visible: visible,
                        description: col.description, ucd: col.ucd, utype: col.utype, unit: col.unit
                    }
                    newMeta.splice(metaDataIndex, 1, meta)
                }
            }

            newMeta = newMeta.filter(function (e) {
                return e
            })
            this.metadata = newMeta;
            this.filterData = filterData;
            this.showCount = false;
            this.dataLoaded = true;

            return [];

        }

        table.convertDataToTabulatorFormat = function (userData, aladinCoordinateFrame) {
            var metadata = [];
            var skyObjectList = userData.overlaySet.skyObjectList;
            var data = [];
            var cooFrame = userData.overlaySet.cooframe || 'J2000';
            var coordinateConversionFunction = function (ra, dec) {
                return [ra, dec];
            }

            if (cooFrame.toLowerCase() === 'galactic' || cooFrame.toLowerCase() === 'gal') {
                coordinateConversionFunction = function (ra, dec) {
                    return @esac.archive.esasky.ifcs.model.coordinatesutils.CoordinatesConversion::convertPointGalacticToJ2000(DD)(ra, dec);
                }
            }
            var i = 0;
            skyObjectList.forEach(function (skyObject) {
                var row = {id: i};
                var ra, dec = undefined;
                Object.keys(skyObject).forEach(function (key) {
                    if (key === "data") {
                        skyObject[key].forEach(function (extraData) {
                            if (i == 0) {
                                metadata.push({
                                    name: extraData.name,
                                    displayName: $wnd.esasky.getColumnDisplayText(extraData.name),
                                    datatype: extraData.type,
                                    visible: true
                                });
                            }
                            if (extraData.type.toUpperCase() === "DOUBLE"
                                || extraData.type.toUpperCase() === "FLOAT"
                                || extraData.type.toUpperCase() === "REAL") {
                                row[extraData.name] = parseFloat(extraData.value);
                                if (isNaN(row[extraData.name])) {
                                    row[extraData.name] = undefined;
                                }
                            } else if (extraData.type.toUpperCase() === "INTEGER" || extraData.type.toUpperCase() === "INT" || extraData.type.toUpperCase() === "SHORT") {
                                row[extraData.name] = parseInt(extraData.value);
                                if (isNaN(row[extraData.name])) {
                                    row[extraData.name] = undefined;
                                }
                            } else if (extraData.type.toUpperCase() === "BIGINT" || extraData.type.toUpperCase() === "LONG") {
                                row[extraData.name] = extraData.value;
                            } else {
                                row[extraData.name] = extraData.value;
                            }
                        });

                    } else if (key !== 'id') {
                        if (key.toLowerCase() === 'ra' || key.toLowerCase() === 'ra_deg') {
                            ra = skyObject[key];
                            if (dec) {
                                setRaDec(ra, dec, row, metadata, i == 0);
                            }
                        } else if (key.toLowerCase() === 'dec' || key.toLowerCase() === 'dec_deg') {
                            dec = skyObject[key];
                            if (ra) {
                                setRaDec(ra, dec, row, metadata, i == 0);
                            }
                        } else {
                            row[key] = skyObject[key];
                            if (i == 0) {
                                metadata.push({
                                    name: key,
                                    displayName: $wnd.esasky.getColumnDisplayText(key),
                                    datatype: "STRING",
                                    visible: true
                                });
                            }
                        }
                    }
                });
                i++;
                data.push(row);
            });

            function setRaDec(ra, dec, row, metadata, shouldAddMetaData) {
                convertedCoordinate = coordinateConversionFunction(ra, dec);
                row["ra_deg"] = parseFloat(convertedCoordinate[0]);
                row["dec_deg"] = parseFloat(convertedCoordinate[1]);
                if (shouldAddMetaData) {
                    metadata.push({
                        name: "ra_deg",
                        displayName: $wnd.esasky.getColumnDisplayText("RA_J2000"),
                        datatype: "DOUBLE",
                        visible: true
                    });
                    metadata.push({
                        name: "dec_deg",
                        displayName: $wnd.esasky.getColumnDisplayText("DEC_J2000"),
                        datatype: "DOUBLE",
                        visible: true
                    });
                }
            }

            table.metadata = metadata;

            return data;

        };

        table.filterData = [];
        table.showCount = false;
        table.dataLoaded = false;

        table.element.onmouseleave = function () {
            wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::onTableMouseLeave()()
        };
        $doc.getElementById(divId + "_rowCount").addEventListener("mouseover", function () {
            wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::onRowCountFooterMouseOver()()
        });


        return table;
    }-*/;


    private native JavaScriptObject getFilterButtonFunc() /*-{
        return function (filterButtonId) {
            var filterButton = $wnd.$("" + "<div id=\'" + filterButtonId
                + "\' class=\"filterButton defaultEsaSkyButton darkStyle smallButton squaredButton gwt-PushButton-up\" "
                + "title=\"" + "\""
                + "\"" + ">" + "<img src=\"images/filter.png\" class=\"fillParent\" />" + "</div>");

            filterButton.on("mouseenter", function (e) {
                filterButton.toggleClass("gwt-PushButton-up-hovering");
            });
            filterButton.on("mouseleave", function (e) {
                filterButton.toggleClass("gwt-PushButton-up-hovering");
                filterButton.removeClass("gwt-PushButton-down");
            });
            filterButton.on("mouseover", function (e) {
                e.stopPropagation();
            });
            filterButton.on("mousedown", function (e) {
                filterButton.toggleClass("gwt-PushButton-down");
                e.stopPropagation();
            });
            filterButton.on("mouseup", function (e) {
                filterButton.toggleClass("gwt-PushButton-down");
                e.stopPropagation();
            });
            return filterButton;
        }
    }-*/;


    private native JavaScriptObject getNumericFilterEditorFunc(TabulatorWrapper wrapper, GeneralJavaScriptObject table, String divId) /*-{
        return function (cell, onRendered, success, cancel, editorParams) {
            var filterButtonId = divId + "_" + editorParams["tapName"];
            var filterButton = wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::getFilterButtonFunc()()(filterButtonId);

            var functionObject = {};
            functionObject.onChange = function (filter) {
                success(filter);
                onFilterChanged(filter);
            }
            filterButton.on("click", function (e) {
                e.stopPropagation();
                var minVal = Infinity;
                var maxVal = -Infinity;

                name = cell.getColumn()._column.definition.field;
                if (table.filterData[name]) {
                    minVal = table.filterData[name].min;
                    maxVal = table.filterData[name].max;
                } else {
                    cell.getColumn()._column.table.rowManager.rows.forEach(function (row) {

                        if (row.data.hasOwnProperty(name) && row.data[name] != undefined) {
                            minVal = Math.min(minVal, row.data[name])
                            maxVal = Math.max(maxVal, row.data[name])
                        }
                    });
                }

                if (minVal == Infinity) {
                    minVal = -100;
                    maxVal = 100;
                }

                wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::toggleNumericFilterDialog(Ljava/lang/String;DD)
                (editorParams["tapName"], minVal, maxVal);
            });

            wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::createNumericFilterDialog(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lesac/archive/esasky/ifcs/model/client/GeneralJavaScriptObject;Lesac/archive/esasky/ifcs/model/client/GeneralJavaScriptObject;Lesac/archive/esasky/ifcs/model/client/GeneralJavaScriptObject;)
            (editorParams["tapName"], editorParams["title"], filterButtonId, functionObject, cell.getColumn().getDefinition().formatter, cell.getColumn().getDefinition().formatterParams);


            var container = $wnd.$("<span></span>")

            container.append(filterButton);

            //create and style input

            function onFilterChanged(input) {
                values = input.split(",");
                var filter = "";
                if (values[0].length > 0) {
                    filter += cell.getField() + " >=  " + values[0]
                }
                if (values.length > 1 && values[1].length > 0) {
                    if (filter.length > 0) {
                        filter += " AND ";
                    }
                    filter += cell.getField() + " <=  " + values[1]
                }
                wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::onFilterChanged(*)(cell.getField(), filter);
            }

            return container[0];

        }
    }-*/;


    private native JavaScriptObject getDateFilterEditorFunc(TabulatorWrapper wrapper, GeneralJavaScriptObject table, String divId) /*-{
        return function (cell, onRendered, success, cancel, editorParams) {
            var filterButtonId = divId + "_" + editorParams["tapName"];
            var filterButton = wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::getFilterButtonFunc()()(filterButtonId);

            var functionObject = {};
            functionObject.onChange = function (filter) {
                success(filter);
                onFilterChanged(filter);
            }

            filterButton.on("click", function (e) {
                e.stopPropagation();
                var minVal = "2100-01-01";
                var maxVal = "1800-01-01";

                name = cell.getColumn()._column.definition.field;
                if (table.filterData[name]) {
                    minVal = table.filterData[name].min;
                    maxVal = table.filterData[name].max;
                } else {
                    cell.getColumn()._column.table.rowManager.rows.forEach(function (row) {

                        if (row.data[name] && row.data[name] != undefined) {
                            if (minVal > row.data[name]) {
                                minVal = row.data[name];
                            }
                            if (maxVal < row.data[name]) {
                                maxVal = row.data[name];
                            }
                        }
                    });
                }

                if (minVal > maxVal) {
                    tmp = minVal
                    minVal = maxVal
                    maxVal = tmp;
                }

                wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::toggleDateFilterDialog(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)
                (editorParams["tapName"], minVal, maxVal);
            });

            wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::createDateFilterDialog(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lesac/archive/esasky/ifcs/model/client/GeneralJavaScriptObject;)
            (editorParams["tapName"], editorParams["title"], filterButtonId, functionObject);
            var container = $wnd.$("<span></span>")

            container.append(filterButton);

            //create and style input

            function onFilterChanged(input) {
                values = input.split(",");
                var filter = "";
                if (values[0].length > 0) {
                    filter += cell.getField() + " >=  '" + values[0] + "'";
                }
                if (values.length > 1 && values[1].length > 0) {
                    if (filter.length > 0) {
                        filter += " AND ";
                    }
                    filter += cell.getField() + " <=  '" + values[1] + "'";
                }
                wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::onFilterChanged(*)(cell.getField(), filter);
            }

            return container[0];

        }
    }-*/;


    private native JavaScriptObject getListFilterEditorFunc(TabulatorWrapper wrapper, GeneralJavaScriptObject table, String divId) /*-{
        return function (cell, onRendered, success, cancel, editorParams) {

            var filterButtonId = divId + "_" + editorParams["tapName"];
            var filterButton = wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::getFilterButtonFunc()()(filterButtonId);

            var functionObject = {};
            functionObject.onChange = function (filter) {
                success(filter);
                onFilterChanged(filter);
            }

            filterButton.on("click", function (e) {
                e.stopPropagation();
                if (table.filterData != []) {
                    name = cell.getColumn()._column.definition.field;
                    list = table.filterData[name]["list"];
                    wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::showListFilterDialog(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lesac/archive/esasky/ifcs/model/client/GeneralJavaScriptObject;)
                    (editorParams["tapName"], editorParams["title"], filterButtonId, minVal + "," + maxVal, functionObject);
                }
            });
            var container = $wnd.$("<span></span>")

            container.append(filterButton);

            //create and style input

            function onFilterChanged(input) {
                values = input.split(",");
                var filter = "";
                if (values[0].length > 0) {
                    filter += cell.getField() + " = ''" + values[0] + "''";
                }
                wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::onFilterChanged(*)(cell.getField(), filter);
            }

            return container[0];

        }
    }-*/;

    private native JavaScriptObject getRaFormatterFunc() /*-{
        return function (cell, formatterParams, onRendered) {
            var raDeg = cell.getValue();
            if (formatterParams.convertBack && formatterParams.convertBack === true) {
                return @esac.archive.esasky.cl.web.client.model.RaPosition::construct(Ljava/lang/String;)(raDeg)
                    .@esac.archive.esasky.cl.web.client.model.RaPosition::getRaDeg()();
            } else {
                if (raDeg === undefined || raDeg === "") {
                    return "";
                }
                if (@esac.archive.esasky.cl.web.client.status.GUISessionStatus::isShowingCoordinatesInDegrees()()) {
                    return @esac.archive.esasky.cl.web.client.model.RaPosition::construct(D)(raDeg)
                        .@esac.archive.esasky.cl.web.client.model.RaPosition::getDegreeString()();
                } else {
                    return @esac.archive.esasky.cl.web.client.model.RaPosition::construct(D)(raDeg)
                        .@esac.archive.esasky.cl.web.client.model.RaPosition::getHmsString()();
                }
            }
        }
    }-*/;


    private native JavaScriptObject getDecFormatterFunc() /*-{
        return function (cell, formatterParams, onRendered) {
            var decDeg = cell.getValue();
            if (formatterParams.convertBack && formatterParams.convertBack === true) {
                return @esac.archive.esasky.cl.web.client.model.DecPosition::construct(Ljava/lang/String;)(decDeg)
                    .@esac.archive.esasky.cl.web.client.model.DecPosition::getDecDeg()();
            } else {
                if (decDeg === undefined || decDeg === "") {
                    return "";
                }
                if (@esac.archive.esasky.cl.web.client.status.GUISessionStatus::isShowingCoordinatesInDegrees()()) {
                    return @esac.archive.esasky.cl.web.client.model.DecPosition::construct(D)(decDeg)
                        .@esac.archive.esasky.cl.web.client.model.DecPosition::getDegreeString()();
                } else {
                    return @esac.archive.esasky.cl.web.client.model.DecPosition::construct(D)(decDeg)
                        .@esac.archive.esasky.cl.web.client.model.DecPosition::getSymbolDmsString()();
                }
            }
        }
    }-*/;

    private native JavaScriptObject getFileSizeFormatterFunc(GeneralJavaScriptObject wrapper) /*-{
        return function (cell, formatterParams, onRendered) {
            var value = cell.getValue();
            var missionName = wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::getMission()()
            if (formatterParams.convertBack && formatterParams.convertBack === true) {
                if (missionName.includes("MAST")) { //MAST has incorrect unit for access_estsize. Should be in kilobytes
                    return @esac.archive.esasky.cl.web.client.utility.SizeFormatter::formatToBytes(Ljava/lang/String;)(value);
                } else {
                    return @esac.archive.esasky.cl.web.client.utility.SizeFormatter::formatToBytes(Ljava/lang/String;)(value) / 1024;
                }
            } else {
                if (value === undefined || value === "") {
                    return "";
                }
                if (missionName.includes("MAST-")) { //MAST has incorrect unit for access_estsize. Should be in kilobytes
                    return @esac.archive.esasky.cl.web.client.utility.SizeFormatter::formatBytes(II)(value, 0);
                } else {
                    return @esac.archive.esasky.cl.web.client.utility.SizeFormatter::formatBytes(II)(value * 1024, 0);
                }
            }
        }
    }-*/;

    private native JavaScriptObject getDoubleFormatterFunc() /*-{
        return function (cell, formatterParams, onRendered) {
            if (formatterParams.convertBack && formatterParams.convertBack === true) {
                return @esac.archive.esasky.cl.web.client.view.resultspanel.tab.filter.NumberValueFormatter::formatStringToDouble(Ljava/lang/String;I)(cell.getValue(), formatterParams.maxDecimalDigits);
            } else {
                if (cell.getValue() == undefined) {
                    return "";
                }
                return @esac.archive.esasky.cl.web.client.view.resultspanel.tab.filter.NumberValueFormatter::formatDouble(DI)(cell.getValue(), formatterParams.maxDecimalDigits);
            }
        }
    }-*/;

    private native JavaScriptObject getPercentFormatterFunc() /*-{
        return function (cell, formatterParams, onRendered) {
            if (formatterParams.convertBack && formatterParams.convertBack === true) {
                return parseFloat(cell.getValue()) / 100;
            } else {
                if (cell.getValue() == undefined) {
                    return "";
                }
                return Number(cell.getValue()).toLocaleString(undefined, {
                    style: 'percent',
                    maximumFractionDigits: formatterParams.maxDecimalDigits
                });
            }
        }
    }-*/;

    private native JavaScriptObject getHideNonDatabaseColumnFormatterFunc() /*-{
        return function (cell, formatterParams, onRendered) {
            if ($wnd.esasky.nonDatabaseColumns.includes(cell.getValue())) {
                return "";
            }
            return cell.getValue();
        }
    }-*/;

    private native JavaScriptObject getDoubleFilterFunc() /*-{
        return function (headerValue, rowValue, rowData, filterParams) {

            var split = headerValue.split(",");

            if (split.length == 2) {

                if (rowValue == null) {
                    // If any filter is added Null should be removed
                    return false;
                }

                var startTrue = true;
                var endTrue = true;
                if (split[0].length > 0 && rowValue < parseFloat(split[0])) {
                    startTrue = false;
                }
                if (split[1].length > 0 && rowValue > parseFloat(split[1])) {

                    endTrue = false;
                }
                return startTrue && endTrue;
            }
            return true;
        }
    }-*/;


    private native JavaScriptObject getDateFilterFunc() /*-{
        return function (headerValue, rowValue, rowData, filterParams) {

            var split = headerValue.split(",");
            if (split.length == 2) {
                if (!rowValue) {
                    return false;
                }
                var startTrue = true;
                var endTrue = true;
                if (split[0].length > 0 && rowValue < split[0]) {
                    startTrue = false;
                }
                if (split[1].length > 0 && rowValue > split[1]) {
                    endTrue = false;
                }
                return startTrue && endTrue;
            }
            return true;
        }
    }-*/;

    private native JavaScriptObject getStringFilterEditorFunc(TabulatorWrapper wrapper) /*-{
        return function (cell, onRendered, success, cancel, editorParams) {
            var editor = this.table.modules.edit.editors["input"];

            var successFunc = function (filter) {
                success(filter);
                var filterString = cell.getField() + " like '%" + filter + "%'";

                wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::onFilterChanged(*)(cell.getField(), filterString);
            }

            var cancelFunc = function () {
                wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::onFilterChanged(*)(cell.getField(), "");
                cancel();
            }

            return editor(cell, onRendered, successFunc, cancelFunc, editorParams);
        }
    }-*/;

    private native JavaScriptObject getBooleanFilterEditorFunc(TabulatorWrapper wrapper) /*-{
        return function (cell, onRendered, success, cancel, editorParams) {

            var editor = this.table.modules.edit.editors["input"];

            var successFunc = function (filter) {
                success(filter);
                var filterString = cell.getField() + " = '" + filter + "'";
                wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::onFilterChanged(*)(cell.getField(), filterString);
            }

            var cancelFunc = function () {
                wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::onFilterChanged(*)(cell.getField(), "");
                cancel();
            }

            return editor(cell, onRendered, successFunc, cancelFunc, editorParams);
        }
    }-*/;

    private native JavaScriptObject getGroupHeaderFunc() /*-{
        return function (value, count, data, group) {
            if (value == "Outside Field of View") {
                return "<span style='color:#777777;'>" + value + "</span><span style='color:#777777; margin-left:10px;'>(" + count + " images)</span>";
            } else if (value == "In Field of View") {
                return "<span style='color:#4EB265;'>" + value + "</span><span style='color:#4EB265; margin-left:10px;'>(" + count + " images)</span>";
            } else {
                return "<span style='color:#4EB265;'>" + value + "</span><span style='color:#4EB265; margin-left:10px;'>(" + count + " tables)</span>";
            }
        }
    }-*/;

    private native JavaScriptObject getGroupStartOpenFunc() /*-{
        return function (value, count, data, group) {
            return value == "In Field of View";
        }
    }-*/;

    private native JavaScriptObject getDataFilteredFunc(TabulatorWrapper wrapper) /*-{
        return function (filters, rows) {
            var returnString = "";
            for (var i = 0; i < rows.length; i++) {
                returnString += rows[i].getIndex() + ",";
            }

            wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::onDataFiltered(Ljava/lang/String;)(returnString);

            var footerCounter = this.footerManager.element.getElementsByClassName("footerCounter")[0];
            var text = $wnd.esasky.getInternationalizationText("tabulator_rowCount");
            text = text.replace("$count$", @esac.archive.esasky.cl.web.client.view.resultspanel.tab.filter.NumberValueFormatter::formatDouble(DI)(rows.length, 0));
            if (footerCounter) {
                footerCounter.innerHTML = text;
            }
            if (!wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::isMOCMode()()) {
                if (rows.length == 0 && this.getHeaderFilters().length > 0) {
                    wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::setPlaceholderText(Lesac/archive/esasky/ifcs/model/client/GeneralJavaScriptObject;Ljava/lang/String;)(this, $wnd.esasky.getInternationalizationText("tabulator_filtered_empty"));
                }
                if (this.filteredOnFov) {
                    wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::setPlaceholderText(Lesac/archive/esasky/ifcs/model/client/GeneralJavaScriptObject;Ljava/lang/String;)(this, $wnd.esasky.getInternationalizationText("tabulator_no_images"));
                }
            }
        }
    }-*/;

    private native JavaScriptObject getDataLoadedFunc(TabulatorWrapper wrapper, JavaScriptObject settings) /*-{
        return function (data) {
            var imageButtonFormatter = function (cell, formatterParams, onRendered) {
                var isDisabled = false;

                if (formatterParams.isDisabledFunc) {
                    isDisabled = formatterParams.isDisabledFunc(cell);
                }

                var disabledClass = isDisabled ? "buttonCellDisabled" : "";

                var toolTip = formatterParams.tooltip;
                if (isDisabled && formatterParams.disabledTooltip) {
                    toolTip = formatterParams.disabledTooltip;
                }

                return "<div class='buttonCell " + disabledClass + "' title='" + toolTip + "'><img src='images/" + formatterParams.image + "' width='20px' height='20px'/></div>";
            };

            var obscoreButtonDisabled = function (cell) {
                var data = cell.getData();
                var tableNames = data["table_names"];
                if (!tableNames) {
                    return true;
                }
                var tableNameList = tableNames.split(',')

                var disabled = tableNameList.some(function (v) {
                     var table = v.trim().toLowerCase();
                    return table === "ivoa.obscore" || table === "obscore";
                });

                return !disabled;
            };

            if ((data.length === 0 && !wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::isMOCMode())
                || wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::hasBeenClosed()()) {
                return;
            }

            if (data.length === 0 && this.clearing) {
                return;
            }

            this.rowManager.adjustTableSize();
            if (this.dataLoaded && !wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::isMOCMode()() && data.length == 0) {
                if (settings.fovLimitDisabled) {
                    wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::setPlaceholderText(Lesac/archive/esasky/ifcs/model/client/GeneralJavaScriptObject;Ljava/lang/String;)(this, $wnd.esasky.getInternationalizationText("tabulator_no_data_fov_limit_disabled"));
                } else {
                    wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::setPlaceholderText(Lesac/archive/esasky/ifcs/model/client/GeneralJavaScriptObject;Ljava/lang/String;)(this, $wnd.esasky.getInternationalizationText("tabulator_no_data"));
                }
            } else if (!wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::isMOCMode()()) {
                this.options.placeholder.innerText = "";
            }
            wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::onDataLoaded()();

            // Fix for setting the access_url and preview column next to the center button
            // until we get some better handling of external tap metadata
            // preview column is from ASTRON
            var accessUrlColumn = this.getColumn("access_url");
            if (accessUrlColumn && settings.isDownloadable) {
                accessUrlColumn.move("centre", true);
            }
            var previewColumn = this.getColumn("preview");
            if (previewColumn) {
                previewColumn.move("centre", true);
            }

            if (settings.addObscoreTableColumn) {
                this.addColumn({
                    title: "Add",
                    field: "obscoreAddBtn",
                    visible: true,
                    headerSort: false,
                    headerTooltip: "Add ObsCore table to External Data Center Panel",
                    minWidth: 55,
                    download: false,
                    width: 40,
                    hozAlign: "center",
                    formatter: imageButtonFormatter,
                    formatterParams: {
                        image: "plus-sign-light-small.png",
                        tooltip: "Add ObsCore table to the Treemap",
                        disabledTooltip: "Add ObsCore table to the Treemap",
                        isDisabledFunc: obscoreButtonDisabled
                    },
                    cellClick: function (e, cell) {
                        e.stopPropagation();
                        if (obscoreButtonDisabled(cell) === false) {
                            wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::onAddObscoreTableClicked(*)(cell.getData());
                        }
                    }
                }, true);
            }

            if (settings.addAdqlColumn) {
                this.addColumn({
                    title: "Query",
                    field: "adqlBtn",
                    visible: true,
                    headerSort: false,
                    headerTooltip: "ADQL query",
                    minWidth: 65,
                    download: false,
                    width: 40,
                    hozAlign: "center",
                    formatter: imageButtonFormatter,
                    formatterParams: {image: "query-icon.png", tooltip: "Create a custom ADQL query"},
                    cellClick: function (e, cell) {
                        e.stopPropagation();
                        wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::onAdqlClicked(Lesac/archive/esasky/ifcs/model/client/GeneralJavaScriptObject;)(cell.getData());
                    }
                }, true);
            }

            if (settings.addMetadataColumn) {
                this.addColumn({
                    title: "Columns",
                    field: "metadataBtn",
                    visible: true,
                    headerSort: false,
                    headerTooltip: "Column metadata",
                    minWidth: 75,
                    download: false,
                    width: 40,
                    hozAlign: "center",
                    formatter: imageButtonFormatter,
                    formatterParams: {image: "column_icon.png", tooltip: "Show column metadata"},
                    cellClick: function (e, cell) {
                        e.stopPropagation();
                        wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::onMetadataClicked(*)(cell.getData());
                    }
                }, true);
            }

            if (settings.addOpenTableColumn) {
                this.addColumn({
                    title: "Open",
                    field: "openTableBtn",
                    visible: true,
                    headerSort: false,
                    headerTooltip: "Open table",
                    minWidth: 60,
                    download: false,
                    width: 40,
                    hozAlign: "center",
                    formatter: imageButtonFormatter,
                    formatterParams: {image: "table-icon.png", tooltip: "Open table"},
                    cellClick: function (e, cell) {
                        e.stopPropagation();
                        wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::onOpenTableClicked(*)(cell.getData());
                    }
                }, true);
            }
        }
    }-*/;

    private native JavaScriptObject getDataLoadingFunc(TabulatorWrapper wrapper, String divId, JavaScriptObject settings) /*-{
        return function (data) {
            var columnDef = [];

            if ((data.length === 0 && !wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::isMOCMode())
                || wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::hasBeenClosed()()) {
                return;
            }

            if (data.length === 0 && this.clearing) {
                return;
            }


            var descriptorMetadata = wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::getDescriptorMetaData()();
            var activeColumnGroup = [];
            var isSSO = false;
            var imageButtonFormatter = function (cell, formatterParams, onRendered) {
                return "<div class='buttonCell' title='" + formatterParams.tooltip + "'><img src='images/" + formatterParams.image + "'/></div>";
            };



            var raName = "";
            var decName = "";
            if (this.metadata) {
                var raColumnName = wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::getRaColumnName()();
                var decColumnName = wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::getDecColumnName()();

                for (var i = 0; i < this.metadata.length; i++) {
                    if (this.metadata[i].name === raColumnName) {
                        raName = this.metadata[i].name;
                    } else if (this.metadata[i].name === decColumnName) {
                        decName = this.metadata[i].name;
                    }

                }
            }

            // Add additional columns from settings
            if (settings.addSelectionColumn) {
                activeColumnGroup.push({
                    formatter: "rowSelection",
                    titleFormatterParams: {title: settings.selectionHeaderTitle},
                    field: "rowSelection",
                    visible: descriptorMetadata && descriptorMetadata.rowSelection ? descriptorMetadata.rowSelection.visible : true,
                    title: "Selection",
                    download: false,
                    titleFormatter: "rowSelection",
                    sorter: function (a, b, aRow, bRow, column, dir, sorterParams) {
                        return bRow.isSelected() - aRow.isSelected();
                    }
                });
            }
            if (raName !== "" && decName !== "" && !settings.disableGoToColumn) {
                activeColumnGroup.push({
                    title: $wnd.esasky.getInternationalizationText("tabulator_centreHeader"),
                    field: "centre",
                    visible: descriptorMetadata && descriptorMetadata.centre ? descriptorMetadata.centre.visible : true,
                    headerSort: false,
                    headerTooltip: $wnd.esasky.getInternationalizationText("tabulator_centreHeaderTooltip"),
                    minWidth: 50,
                    download: false,
                    formatter: imageButtonFormatter, width: 40, hozAlign: "center", formatterParams: {
                        image: "recenter.png",
                        tooltip: $wnd.esasky.getInternationalizationText("tabulator_centreOnCoordinates")
                    },
                    cellClick: function (e, cell) {
                        e.stopPropagation();
                        var data = cell.getData();
                        wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::onCenterClicked(Lesac/archive/esasky/ifcs/model/client/GeneralJavaScriptObject;)(data);
                    }
                });
            }
            if (settings.addSendToVOApplicationColumn) {
                activeColumnGroup.push({
                    title: $wnd.esasky.getInternationalizationText("tabulator_sendToVOApplicationHeader"),
                    field: "samp",
                    visible: descriptorMetadata && descriptorMetadata.samp ? descriptorMetadata.samp.visible : true,
                    headerSort: false,
                    headerTooltip: $wnd.esasky.getInternationalizationText("tabulator_sendRowToVOApplicationHeaderTooltip"),
                    minWidth: 50,
                    download: false,
                    formatter: imageButtonFormatter, width: 40, hozAlign: "center", formatterParams: {
                        image: "send_small.png",
                        tooltip: $wnd.esasky.getInternationalizationText("tabulator_sendRowToVOA")
                    },
                    cellClick: function (e, cell) {
                        e.stopPropagation();
                        wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::onSendToVoApplicaitionClicked(Lesac/archive/esasky/ifcs/model/client/GeneralJavaScriptObject;)(cell.getData());
                    }
                });
            }

            if (settings.addLink2ArchiveColumn) {
                activeColumnGroup.push({
                    title: $wnd.esasky.getInternationalizationText("tabulator_link2ArchiveHeader"),
                    field: "link2archive",
                    visible: descriptorMetadata && descriptorMetadata.link2archive ? descriptorMetadata.link2archive.visible : true,
                    headerSort: false,
                    headerTooltip: $wnd.esasky.getInternationalizationText("tabulator_link2ArchiveHeaderTooltip"),
                    minWidth: 63,
                    download: false,
                    formatter: imageButtonFormatter, width: 40, hozAlign: "center", formatterParams: {
                        image: "link2archive.png",
                        tooltip: $wnd.esasky.getInternationalizationText("tabulator_link2ArchiveButtonTooltip")
                    },
                    cellClick: function (e, cell) {
                        e.stopPropagation();
                        wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::onLink2ArchiveClicked(Lesac/archive/esasky/ifcs/model/client/GeneralJavaScriptObject;)(cell.getRow());
                    }
                });
            }
            if (settings.addDatalinkLink2ArchiveColumn) {
                activeColumnGroup.push({
                    title: $wnd.esasky.getInternationalizationText("tabulator_products"),
                    titleDownload: $wnd.esasky.getInternationalizationText("tabulator_products"),
                    field: "link2archive",
                    visible: descriptorMetadata && descriptorMetadata.link2archive ? descriptorMetadata.link2archive.visible : true,
                    headerSort: false,
                    headerTooltip: $wnd.esasky.getInternationalizationText("tabulator_columnHeader_browseProducts"),
                    minWidth: 85,
                    download: true,
                    formatter: imageButtonFormatter, width: 40, hozAlign: "center", formatterParams: {
                        image: "download_small.png",
                        tooltip: $wnd.esasky.getInternationalizationText("tabulator_browseProducts")
                    },
                    cellClick: function (e, cell) {
                        e.stopPropagation();
                        wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::onLink2ArchiveClicked(Lesac/archive/esasky/ifcs/model/client/GeneralJavaScriptObject;)(cell.getRow());
                    }
                });
            }
            if (settings.addLink2AdsColumn) {
                activeColumnGroup.push({
                    title: $wnd.esasky.getInternationalizationText("tabulator_link2AdsHeader"),
                    field: "link2archive",
                    visible: descriptorMetadata && descriptorMetadata.link2archive ? descriptorMetadata.link2archive.visible : true,
                    headerSort: false,
                    headerTooltip: $wnd.esasky.getInternationalizationText("tabulator_link2AdsHeaderTooltip"),
                    minWidth: 63,
                    download: false,
                    formatter: imageButtonFormatter, width: 40, hozAlign: "center", formatterParams: {
                        image: "link2archive.png",
                        tooltip: $wnd.esasky.getInternationalizationText("tabulator_link2AdsButtonTooltip")
                    },
                    cellClick: function (e, cell) {
                        e.stopPropagation();
                        wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::onLink2ArchiveClicked(Lesac/archive/esasky/ifcs/model/client/GeneralJavaScriptObject;)(cell.getRow());
                    }
                });
            }
            if (settings.addSourcesInPublicationColumn) {
                activeColumnGroup.push({
                    title: $wnd.esasky.getInternationalizationText("tabulator_SourcesInPublicationHeader"),
                    field: "sourcesInPublication",
                    visible: descriptorMetadata && descriptorMetadata.sourcesInPublication ? descriptorMetadata.sourcesInPublication.visible : true,
                    headerSort: false,
                    headerTooltip: $wnd.esasky.getInternationalizationText("tabulator_SourcesInPublicationHeaderTooltip"),
                    minWidth: 67,
                    download: false,
                    formatter: imageButtonFormatter, width: 40, hozAlign: "center", formatterParams: {
                        image: "target_list.png",
                        tooltip: $wnd.esasky.getInternationalizationText("tabulator_SourcesInPublication")
                    },
                    cellClick: function (e, cell) {
                        e.stopPropagation();
                        wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::onSourcesInPublicationClicked(Lesac/archive/esasky/ifcs/model/client/GeneralJavaScriptObject;)(cell.getData());
                    }
                });
            }
            if (this.addHipsColumn) {
                activeColumnGroup.push({
                    title: $wnd.esasky.getInternationalizationText("tabulator_addHipsColumn"),
                    field: "addHipsColumn",
                    visible: descriptorMetadata && descriptorMetadata.sourcesInPublication ? descriptorMetadata.sourcesInPublication.visible : true,
                    headerSort: false,
                    headerTooltip: $wnd.esasky.getInternationalizationText("tabulator_SourcesInPublicationHeaderTooltip"),
                    minWidth: 50,
                    download: false,
                    formatter: imageButtonFormatter, width: 40, hozAlign: "center", formatterParams: {
                        image: "plus-sign-light-small.png",
                        tooltip: $wnd.esasky.getInternationalizationText("tabulator_addHips_tooltip")
                    },
                    cellClick: function (e, cell) {
                        e.stopPropagation();
                        wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::onAddHipsClicked(Lesac/archive/esasky/ifcs/model/client/GeneralJavaScriptObject;)(cell.getData());
                    }
                });
            }

            if (this.metadata) {
                for (var i = 0; i < this.metadata.length; i++) {
                    var shouldHideColumn = false;
                    for (var j = 0; j < $wnd.esasky.databaseColumnsToHide.length; j++) {
                        if (this.metadata[i].name.toLowerCase() === $wnd.esasky.databaseColumnsToHide[j]) {
                            shouldHideColumn = true;
                        }
                    }

                    if (this.metadata[i].name.toLowerCase() === "observation_oid" && isSSO) {
                        shouldHideColumn = true;
                    }

                    if (shouldHideColumn && this.isEsaskyData) {
                        activeColumnGroup.push({
                            download: false,
                            field: this.metadata[i].name,
                            visible: false
                        });
                        continue;
                    }

                    if (this.metadata[i].name.toLowerCase() === "sso_name_splitter") {
                        isSSO = true;
                        columnDef.push(activeColumnGroup[0]); //Selection column
                        columnDef.push({
                            title: $wnd.esasky.getInternationalizationText("tableGroup_Observation"),
                            columns: activeColumnGroup.slice(1)
                        });
                        activeColumnGroup = [];
                        columnDef.push({
                            title: @esac.archive.esasky.cl.web.client.status.GUISessionStatus::getTrackedSsoName()(),
                            columns: activeColumnGroup
                        });
                    } else {
                        activeColumnGroup.push(wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::generateColumn(*)(wrapper, this, this.metadata[i], divId, this.metadata.find(function (obj) {
                            return obj.name === "access_format"
                        })));
                    }
                }
                if (!isSSO) {
                    columnDef = activeColumnGroup;
                }

                this.setColumns(columnDef);
                this.getColumns().forEach(function (column) {
                    if (column.getDefinition().sorter) {
                        column.getElement().onmouseover = function () {
                            column.getElement().style.backgroundColor = "#d0d0d0";
                        }
                        column.getElement().onmouseout = function () {
                            column.getElement().style.backgroundColor = "";
                        }
                    }
                });
            }
        }
    }-*/;


    private native JavaScriptObject getRowSelectionChangedFunc(TabulatorWrapper wrapper) /*-{
        var previouslySelectedMap = [];
        var selectionMap = [];
        return function (data, rows) {
            var table = this;
            selectionMap = [];

            wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::multiSelectionInProgress()();
            rows.forEach(function (item, index, array) {
                selectionMap[item.getIndex()] = true;
                if (!previouslySelectedMap[item.getIndex()]) {
                    wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::onRowSelection(Lesac/archive/esasky/ifcs/model/client/GeneralJavaScriptObject;)(item);
                }
            });
            Object.keys(previouslySelectedMap).forEach(function (item, index, array) {
                if (!selectionMap[item] && table.getRow(item)) {
                    wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::onRowDeselection(Lesac/archive/esasky/ifcs/model/client/GeneralJavaScriptObject;)(table.getRow(item));
                }
            });
            previouslySelectedMap = selectionMap;
            wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::multiSelectionFinished()();
        }
    }-*/;


    private native JavaScriptObject createDefaultColumn(JavaScriptObject wrapper, JavaScriptObject columnMeta, JavaScriptObject formatter, JavaScriptObject formatterParams, JavaScriptObject headerFilter, JavaScriptObject headerFilterFunc, String sorter) /*-{
        return wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::createCustomColumn(*)(wrapper, columnMeta, formatter, formatterParams, headerFilter, headerFilterFunc, sorter, columnMeta.displayName, columnMeta.description, undefined);

    }-*/;

    private native JavaScriptObject createCustomColumn(JavaScriptObject wrapper, JavaScriptObject columnMeta, JavaScriptObject formatter, JavaScriptObject formatterParams, JavaScriptObject headerFilter, JavaScriptObject headerFilterFunc, String sorter, String title, String headerTooltip, JavaScriptObject cellClick) /*-{

        return {
            title: title ? title : columnMeta.displayName,
            titleDownload: columnMeta.name,
            field: columnMeta.name,
            visible: (columnMeta.visible === undefined) ? columnMeta.principal : columnMeta.visible,
            headerTooltip: headerTooltip ? headerTooltip : columnMeta.description,
            download: true,
            formatter: formatter,
            formatterParams: formatterParams,
            sorter: sorter,
            sorterParams: {thousandSeperator: ""},
            headerFilter: headerFilter,
            headerFilterParams: {
                tapName: columnMeta.name,
                title: columnMeta.displayName
            },
            headerFilterFunc: headerFilterFunc,
            headerFilterFuncParams: {tapName: columnMeta.name},
            cellClick: cellClick
        }

    }-*/;

    private native JavaScriptObject generateColumn(JavaScriptObject wrapper, JavaScriptObject table, JavaScriptObject columnMeta, String divId, String accessFormat) /*-{

        // Try to generate column from UCD or UType values
        var column = wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::createColumnWithContentDescriptors(*)(wrapper, table, columnMeta, divId, accessFormat);

        // If column is undefined, try to generate column from xtype
        if (!column) {
            column = wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::createColumnWithXType(*)(wrapper, table, columnMeta, divId);
        }

        // If column is undefined, try to generate column from unit
        if (!column) {
            column = wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::createColumnWithUnit(*)(wrapper, table, columnMeta, divId);
        }

        // If column is undefined, try to generate column from data type
        if (!column) {
            column = wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::createColumnWithDatatype(*)(wrapper, table, columnMeta, divId);
        }


        // If column could not be generated, create default column
        if (!column) {
            var formatter = "plaintext";
            var formatterParams = undefined;
            var headerFilter = wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::getStringFilterEditorFunc(*)(wrapper);
            var headerFilterFunc = "like";
            var sorter = "string"
            column = wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::createDefaultColumn(*)(wrapper, columnMeta, formatter, formatterParams, headerFilter, headerFilterFunc, sorter);
        }


        return column;


    }-*/;

    private native JavaScriptObject createColumnWithContentDescriptors(JavaScriptObject wrapper, JavaScriptObject table, JavaScriptObject columnMeta, String divId, String accessFormat) /*-{
        var formatter, formatterParams, headerFilter, headerFilterFunc, sorter, title, tooltip, cellClick;
        var ucd = columnMeta.ucd ? columnMeta.ucd : "";
        var utype = columnMeta.utype ? columnMeta.utype : "";
        if ((ucd.includes("phys.size") && ucd.includes("meta.file")) || utype.includes("Access.Size")) {
            formatter = wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::getFileSizeFormatterFunc(*)(wrapper);
            headerFilter = wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::getNumericFilterEditorFunc(*)(wrapper, table, divId);
            headerFilterFunc = wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::getDoubleFilterFunc()();
            sorter = "number";
            title = $wnd.esasky.getInternationalizationText("tabulator_accessEstSize_header");
            tooltip = $wnd.esasky.getInternationalizationText("tabulator_accessEstSize_headerTooltip");

        } else if (ucd.includes("meta.code.status")) {
            formatter = "plaintext";
            headerFilter = wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::getBooleanFilterEditorFunc(*)(wrapper);
            headerFilterFunc = "like";
            sorter = "string";
        } else if (ucd.includes("pos.eq.dec") || utype.includes("Char.SpatialAxis.Coverage.Location.Coord.Position2D.Value2.C2")) {
            formatter = wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::getDecFormatterFunc()();
            headerFilter = wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::getNumericFilterEditorFunc(*)(wrapper, table, divId);
            headerFilterFunc = wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::getDoubleFilterFunc()();
            sorter = "number";
        } else if (ucd.includes("pos.eq.ra") || utype.includes("Char.SpatialAxis.Coverage.Location.Coord.Position2D.Value2.C1")) {
            formatter = wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::getRaFormatterFunc()();
            headerFilter = wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::getNumericFilterEditorFunc(*)(wrapper, table, divId);
            headerFilterFunc = wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::getDoubleFilterFunc()();
            sorter = "number";
        } else if (ucd.includes("meta.bib.author")) {
            formatter = function (cell, formatterParams, onRendered) {
                return $wnd.esasky.linkListFormatter(cell.getValue(), 100);
            };
            headerFilter = true;
            headerFilterFunc = "like";
            sorter = "string";
            title = $wnd.esasky.getInternationalizationText("Authors");
            tooltip = $wnd.esasky.getInternationalizationText("tabulator_authorHeaderTooltip");
        } else if (ucd.includes("meta.ref.url") && ucd.includes("preview")) {
            title = $wnd.esasky.getInternationalizationText("tabulator_previewHeader");
            tooltip = $wnd.esasky.getInternationalizationText("tabulator_previewHeaderTooltip");
            formatter = function (cell, formatterParams, onRendered) {
                return "<div class='buttonCell' title='" + formatterParams.tooltip + "'><img src='images/" + formatterParams.image + "' width='20px' height='20px'/></div>";
            };
            formatterParams = {
                image: "preview.png",
                tooltip: $wnd.esasky.getInternationalizationText("tabulator_preview")
            }
            cellClick = function (e, cell) {
                e.stopPropagation();
                wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::onPostcardUrlClicked(Lesac/archive/esasky/ifcs/model/client/GeneralJavaScriptObject;Ljava/lang/String;)(cell.getRow(), cell.getColumn()._column.field);
            }
        } else if (((ucd.includes("meta.dataset") || ucd.includes("meta.product")) && ucd.includes("meta.ref.url")) || utype.includes("Access.Reference")) {
            formatter = function (cell, formatterParams, onRendered) {
                return "<div class='buttonCell' title='" + formatterParams.tooltip + "'><img src='images/" + formatterParams.image + "' width='20px' height='20px'/></div>";
            };
            formatterParams = {
                image: "download_small.png",
                tooltip: $wnd.esasky.getInternationalizationText("tabulator_download")
            }

            cellClick = function (e, cell) {
                e.stopPropagation();
                var rowData = cell.getData();
                var cellData = cell.getValue();
                if ((rowData.access_format && rowData.access_format.toLowerCase().includes("datalink"))
                    || (cellData != null && typeof cellData === "string" && cellData.toLowerCase().includes("datalink"))) {
                    wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::onDatalinkClicked(*)(cell.getRow(), cellData);
                } else {
                    wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::onAccessUrlClicked(Ljava/lang/String;)(cell.getValue());
                }
            };
        } else if (ucd.includes("meta.ref.url") && !ucd.includes("meta.curation")) {
            if (accessFormat)  {
                formatter = function (cell, formatterParams, onRendered) {
                    return "<div class='buttonCell' title='" + formatterParams.tooltip + "'><img src='images/" + formatterParams.image + "' width='20px' height='20px'/></div>";
                };
                formatterParams = {
                    image: "download_small.png",
                    tooltip: $wnd.esasky.getInternationalizationText("tabulator_download")
                }
                columnMeta.displayName = "Download";
            } else {
                formatter = function (cell, formatterParams, onRendered) {
                    return "<div class='buttonCell' title='" + formatterParams.tooltip + "'><img src='images/" + formatterParams.image + "' width='20px' height='20px'/></div>";
                };
                formatterParams = {
                    image: "link2archive.png",
                    tooltip: $wnd.esasky.getInternationalizationText("tabulator_link2ArchiveButtonTooltip")
                };
            }

            tooltip = $wnd.esasky.getInternationalizationText("tabulator_link2ArchiveHeaderTooltip");
            cellClick = function (e, cell) {
                e.stopPropagation();
                var rowData = cell.getData();
                var cellData = cell.getValue();
                if ((rowData.access_format && rowData.access_format.toLowerCase().includes("datalink"))
                    || (cellData && typeof cellData === "string" && cellData.toLowerCase().includes("datalink"))) {
                    wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::onDatalinkClicked(*)(cell.getRow(), cellData);
                } else {
                    wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::onLink2ArchiveClicked(Lesac/archive/esasky/ifcs/model/client/GeneralJavaScriptObject;Ljava/lang/String;)(cell.getRow(), columnMeta.name);
                }
            }
        } else if (ucd.includes("time.start") || ucd.includes("time.end")) {
            formatter = "plaintext";
            headerFilter = wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::getDateFilterEditorFunc(*)(wrapper, table, divId);
            headerFilterFunc = wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::getDateFilterFunc()();
            sorter = "string";
        } else {
            return undefined;
        }


        return wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::createCustomColumn(*)(wrapper, columnMeta, formatter, formatterParams, headerFilter, headerFilterFunc, sorter, title, tooltip, cellClick)

    }-*/;


    private native JavaScriptObject createColumnWithXType(JavaScriptObject wrapper, JavaScriptObject table, JavaScriptObject columnMeta, String divId) /*-{
        var formatter, formatterParams, headerFilter, headerFilterFunc, sorter;
        var xtype = columnMeta.xtype ? columnMeta.xtype : "";
        switch (xtype.toLowerCase()) {
            case "adql:timestamp":
                formatter = "plaintext";
                headerFilter = wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::getDateFilterEditorFunc(*)(wrapper, table, divId);
                headerFilterFunc = wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::getDateFilterFunc()();
                sorter = "string";
                break;
            default:
                return undefined;
        }

        return wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::createDefaultColumn(*)(wrapper, columnMeta, formatter, formatterParams, headerFilter, headerFilterFunc, sorter);

    }-*/;

    private native JavaScriptObject createColumnWithUnit(JavaScriptObject wrapper, JavaScriptObject table, JavaScriptObject columnMeta, String divId) /*-{
        var formatter, formatterParams, headerFilter, headerFilterFunc, sorter;
        var unit = columnMeta.unit ? columnMeta.unit : "";
        switch (unit.toLowerCase()) {
            case "time":
                formatter = "plaintext";
                headerFilter = wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::getDateFilterEditorFunc(*)(wrapper, table, divId);
                headerFilterFunc = wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::getDateFilterFunc()();
                sorter = "string";
                break;
            default:
                return undefined;
        }

        return wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::createDefaultColumn(*)(wrapper, columnMeta, formatter, formatterParams, headerFilter, headerFilterFunc, sorter);

    }-*/;

    private native JavaScriptObject createColumnWithDatatype(JavaScriptObject wrapper, JavaScriptObject table, JavaScriptObject columnMeta, String divId) /*-{
        var formatter, formatterParams, headerFilter, headerFilterFunc, sorter;
        var datatype = columnMeta.datatype ? columnMeta.datatype : "";
        switch (datatype.toLowerCase()) {
            case "char":
                formatter = "plaintext";
                headerFilter = wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::getStringFilterEditorFunc(*)(wrapper);
                headerFilterFunc = "like";
                sorter = "string";
                break;
            case "boolean":
                formatter = "plaintext";
                headerFilter = wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::getBooleanFilterEditorFunc(*)(wrapper);
                headerFilterFunc = "like";
                sorter = "string";
                break;
            case "double":
            case "float":
            case "real":
                formatter = wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::getDoubleFormatterFunc()();
                formatterParams = {maxDecimalDigits: columnMeta.maxDecimalDigits || 4};
                headerFilter = wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::getNumericFilterEditorFunc(*)(wrapper, table, divId);
                headerFilterFunc = wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::getDoubleFilterFunc()();
                sorter = "number";
                break;
            case "int":
            case "integer":
            case "short":
            case "long":
            case "bigint":
                formatter = wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::getDoubleFormatterFunc()();
                formatterParams = {maxDecimalDigits: 0};
                headerFilter = wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::getNumericFilterEditorFunc(*)(wrapper, table, divId);
                headerFilterFunc = wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::getDoubleFilterFunc()();
                sorter = "number";
                break;
            case "percent":
                formatter = wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::getPercentFormatterFunc()();
                formatterParams = {maxDecimalDigits: columnMeta.maxDecimalDigits || 4};
                headerFilter = wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::getNumericFilterEditorFunc(*)(wrapper, table, divId);
                headerFilterFunc = wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::getDoubleFilterFunc()();
                sorter = "number";
                break;
            case "list":
                formatter = "plaintext";
                headerFilter = wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::getListFilterEditorFunc(*)(wrapper, table, divId);
                headerFilterFunc = "like";
                break;
            case "datetime":
            case "timestamp":
                formatter = "plaintext";
                headerFilter = wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::getDateFilterEditorFunc(*)(wrapper, table, divId);
                headerFilterFunc = wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::getDateFilterFunc()();
                sorter = "string";
                break;
            case "html":
                formatter = function (cell, formatterParams, onRendered) {
                    if (formatterParams.makeHref) {
                        return "<a href=\"" + cell.getValue() + "\" target=\"blank\">" + cell.getValue() + "</a>"
                    } else {
                        return cell.getValue();
                    }
                };
                formatterParams = {makeHref: columnMeta.makeHref};
                headerFilter = wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::getStringFilterEditorFunc(*)(wrapper);
                headerFilterFunc = "like";
                break;
            case "string_hide_non_database_values":
                formatter = wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::getHideNonDatabaseColumnFormatterFunc()();
                headerFilter = wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::getStringFilterEditorFunc(*)(wrapper);
                headerFilterFunc = "like";
                sorter = "string";
                break;
            default:
                return undefined;
        }

        return wrapper.@esac.archive.esasky.cl.web.client.view.resultspanel.tabulator.TabulatorWrapper::createDefaultColumn(*)(wrapper, columnMeta, formatter, formatterParams, headerFilter, headerFilterFunc, sorter);
    }-*/;

    public void onRowEnter(int rowId) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastHoverTime > 5) {
            lastHoverTime = currentTime;
            tabulatorCallback.onRowMouseEnter(rowId);
            if (lastHoveredRow > -1 && lastHoveredRow != rowId) {
                tabulatorCallback.onRowMouseLeave(lastHoveredRow);
            }
            lastHoveredRow = rowId;
        }
    }

    public void onTableMouseLeave() {
        if (lastHoveredRow > -1) {
            tabulatorCallback.onRowMouseLeave(lastHoveredRow);
        }
    }

    public void onDataLoaded() {
        if (tableJsObject != null) {
            setCorrectFilterBehaviour();
            tabulatorCallback.onDataLoaded(tableJsObject.invokeFunction("getData"), tableJsObject.getProperty("metadata"));
        }
    }

    public void onTableHeightChanged() {
        if (tableJsObject != null) {
            tabulatorCallback.onTableHeightChanged();
        }
    }

    public void onDatalinkClicked(final GeneralJavaScriptObject row, final String url) {
        tabulatorCallback.onDatalinkClicked(row, url);
    }

    public void onAccessUrlClicked(String url) {
        tabulatorCallback.onAccessUrlClicked(url);
    }

    public void onPostcardUrlClicked(final GeneralJavaScriptObject rowData, String columnName) {
        tabulatorCallback.onPostcardUrlClicked(rowData, columnName);
    }

    public void onCenterClicked(final GeneralJavaScriptObject rowData) {
        tabulatorCallback.onCenterClicked(rowData);
    }


    public void onSendToVoApplicaitionClicked(final GeneralJavaScriptObject rowData) {
        tabulatorCallback.onSendToVoApplicaitionClicked(rowData);
    }

    public void onLink2ArchiveClicked(final GeneralJavaScriptObject row) {
        tabulatorCallback.onLink2ArchiveClicked(row);
    }

    public void onLink2ArchiveClicked(final GeneralJavaScriptObject row, final String columnName) {
        tabulatorCallback.onLink2ArchiveClicked(row, columnName);
    }


    public void onSourcesInPublicationClicked(final GeneralJavaScriptObject rowData) {
        tabulatorCallback.onSourcesInPublicationClicked(rowData);
    }

    public void onAdqlClicked(final GeneralJavaScriptObject rowData) {
        tabulatorCallback.onAdqlButtonPressed(rowData);
    }

    public void onMetadataClicked(final GeneralJavaScriptObject rowData) {
        tabulatorCallback.onMetadataButtonPressed(rowData);
    }

    public void onAddObscoreTableClicked(final GeneralJavaScriptObject rowData) {
        tabulatorCallback.onAddObscoreTableClicked(rowData);
    }

    public void onOpenTableClicked(GeneralJavaScriptObject rowData) {
        tabulatorCallback.onOpenTableClicked(rowData);
    }

    public void onAddHipsClicked(final GeneralJavaScriptObject rowData) {
        tabulatorCallback.onAddHipsClicked(rowData);
    }

    public void onRowSelection(GeneralJavaScriptObject row) {
        tabulatorCallback.onRowSelection(row);
    }

    public void onRowDeselection(GeneralJavaScriptObject row) {
        tabulatorCallback.onRowDeselection(row);
    }

    public void onFilterChanged(String label, String filter) {
        tabulatorCallback.onFilterChanged(label, filter);
    }

    public String getLabelFromTapName(String tapName) {
        return tabulatorCallback.getLabelFromTapName(tapName);
    }

    public GeneralJavaScriptObject getDescriptorMetaData() {
        return tabulatorCallback.getDescriptorMetaData();
    }

    public CommonTapDescriptor getDescriptor() {
        return tabulatorCallback.getDescriptor();
    }

    public String getTableName() {
        CommonTapDescriptor descriptor = getDescriptor();
        if (descriptor != null) {
            return descriptor.getTableName();
        } else {
            return null;
        }
    }
    
    public String getMission() {
        return getDescriptor().getMission();
    }

    public boolean isColumnVisible(String columnName, int index) {
        return getDescriptor().getCategory().equals(EsaSkyWebConstants.CATEGORY_PUBLICATIONS)
                || getDescriptor().isColumnVisible(columnName, index);
    }

    public String getColumnUnit(String columnName) {
        return tabulatorCallback.getColumnUnit(columnName);
    }

    public String getRaColumnName() {
        return tabulatorCallback.getRaColumnName();
    }

    public String getDecColumnName() {
        return tabulatorCallback.getDecColumnName();
    }

    public String getUniqueIdentifierField() {
        return tabulatorCallback.getUniqueIdentifierField();
    }

    public void onAjaxResponse() {
        tabulatorCallback.onAjaxResponse();
    }

    public void onAjaxResponseError(String error) {
        tabulatorCallback.onAjaxResponseError(error);
    }

    public void multiSelectionInProgress() {
        tabulatorCallback.multiSelectionInProgress();
    }

    public void multiSelectionFinished() {
        tabulatorCallback.multiSelectionFinished();
    }

    public boolean hasBeenClosed() {
        return tabulatorCallback.hasBeenClosed();
    }

    public void abortRequest() {
        abortRequest(abortController);
    }

    private native void abortRequest(GeneralJavaScriptObject abortController) /*-{
        abortController.abort();
    }-*/;


    public boolean isMOCMode() {
        return tabulatorCallback.isMOCMode();
    }

    public void columnIncludesFilter(String query, String... columns) {
        columnIncludesFilter(tableJsObject, query, columns);
    }

    private native void columnIncludesFilter(GeneralJavaScriptObject table, String query, String... columns) /*-{
        function customFilter(data, params) {
            for (var i = 0; i < params.columns.length; i++) {
                var value = data[params.columns[i]];
                var query = params.query;
                if (value && value.toLowerCase().includes(query.toLowerCase())) {
                    return true;
                }
            }
            return false;
        }

        table.setFilter(customFilter, {query: query, columns: columns})
    }-*/;

    public void disableFilters() {
        disableFilters(tableJsObject);
        filtersShouldBeEnabled = false;
    }


    private native void disableFilters(GeneralJavaScriptObject table) /*-{
        function loopChildren(el) {
            el.classList.add("tabulator-header-filter-disabled");
            el.setAttribute("disabled", true);
            for (var i = 0; i < el.children.length; i++) {
                loopChildren(el.children[i]);
            }
            ;
        }

        var list = table.element.getElementsByClassName("tabulator-header-filter")
        for (var i = 0; i < list.length; i++) {
            loopChildren(list[i]);
        }
        ;
    }-*/;

    public void enableFilters() {
        enableFilters(tableJsObject);
        filtersShouldBeEnabled = true;
    }

    private native void enableFilters(GeneralJavaScriptObject table) /*-{
        var list = Array.from(table.element.getElementsByClassName("tabulator-header-filter-disabled"))
        for (var i = 0; i < list.length; i++) {
            list[i].classList.remove("tabulator-header-filter-disabled");
            list[i].setAttribute("disabled", false);
        }
        ;
    }-*/;

    public void clearFilters(boolean clearUserFilters) {
        clearFilters(tableJsObject, clearUserFilters);
    }

    private native void clearFilters(GeneralJavaScriptObject table, boolean clearUserFilters) /*-{
        table.clearFilter(clearUserFilters);
    }-*/;


    public String getFilterQuery() {
        return getFilterQuery(tableJsObject);
    }

    private native String getFilterQuery(GeneralJavaScriptObject table) /*-{
        var filters = table.getFilters();
        if (filters.length > 0 && filters[0].type) {
            return filters[0].type.query;
        } else {
            return "";
        }
    }-*/;

    public void setCorrectFilterBehaviour() {
        if (filtersShouldBeEnabled) {
            enableFilters();
        } else {
            disableFilters();
        }
    }

    public void registerMocLoadedObserver() {
        MocRepository.getInstance().registerMocLoadedObserver(tabulatorCallback.getEsaSkyUniqId() + "_moc", new MocRepository.MocLoadedObserver() {

            @Override
            public void onLoaded() {
                tableJsObject.setProperty("mocLoaded", true);
                if (tableJsObject.hasProperty("onMocLoaded")) {
                    tableJsObject.invokeFunction("onMocLoaded");
                }
                MocRepository.getInstance().unRegisterMocLoadedObserver(tabulatorCallback.getEsaSkyUniqId() + "_moc");
            }
        });
    }

    public void notifyMocLoadedObserver() {
        MocRepository.getInstance().notifyMocLoaded(tabulatorCallback.getEsaSkyUniqId() + "_header");
    }

    public boolean isDataProductDatalink() {
        return isDataProductDatalink(tableJsObject);
    }

    private native boolean isDataProductDatalink(GeneralJavaScriptObject tableJsObject)/*-{
        var firstRow = tableJsObject.getRows()[0];
        if (!firstRow) {
            return false;
        }
        return firstRow.getData().access_format && firstRow.getData().access_format.toLowerCase().includes("datalink");
    }-*/;

    public void addFilter(String key, String filterString) {
        if (filterDialogs.containsKey(key)) {
            filterDialogs.get(key).setValuesFromString(filterString);
        }
    }

}
