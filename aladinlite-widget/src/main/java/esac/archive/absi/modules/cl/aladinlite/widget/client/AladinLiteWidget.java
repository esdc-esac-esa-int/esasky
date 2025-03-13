package esac.archive.absi.modules.cl.aladinlite.widget.client;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

import esac.archive.absi.modules.cl.aladinlite.widget.client.AladinLiteConstants.CoordinateFrame;
import esac.archive.absi.modules.cl.aladinlite.widget.client.event.*;
import esac.archive.absi.modules.cl.aladinlite.widget.client.model.*;

/**
 * Wrapper panel to hold an Aladin Lite component.
 *
 * @author ileon
 */
public class AladinLiteWidget extends Composite implements AladinLiteCDSWrapper {

	/** Aladin lite gwt div component. */
	private HTML aladinLiteGwtDiv;

	/** Name of the html div for the aladin lite js component. */
	private String aladinLiteDivName;

	/** Aladin Lite javascript object. */
	private JavaScriptObject aladinliteObj;

	/** Component width. */
	private double width;

	/** Component height. */
	private double height;

	/** Sky survey to use. */
	private String surveyString;

	/** Sky survey to use. */
	private JavaScriptObject lastCreatedSurveyJSObj = null;

	/** Frame value (J2000/GAL) */
	private String cooFrameString;

	/** Show layers control?. */
	private boolean showLayersControlBoolean;

	/** Show go to control?. */
	private boolean showGotoControlBoolean;

	/** Show full screen control?. */
	private boolean showFullscreenControlBoolean;

	/** Show share control?. */
	private boolean showShareControlBoolean;

	/** Show reticle?. */
	private boolean showReticleBoolean;

	/** Show zoom control?. */
	private boolean showZoomControlBoolean;

	/** Show frame?. */
	private boolean showFrameBoolean;

	/** Initial position. */
	private String targetString;

	/** Initial zoom. */
	private double zoomDouble;

	/** Is this widget already attached?. */
	private boolean isAttached;

	private String defaultProjection = "SIN";

	private static SimpleEventBus eventBus = null;

	/**
	 * Static variable, otherwise it cannot be accessed from static method, and if
	 * it is not a static method it cannot be called by javascript listener...
	 */
	private static Widget associatedWidget;

	/**
	 * Default constructor.
	 */
	@SuppressWarnings("static-access")
	public AladinLiteWidget(String divName, double width, double height, String surveyString, String cooFrameString,
			boolean showLayersControlBoolean, boolean showGotoControlBoolean, boolean showFullscreenControlBoolean,
			boolean showShareControlBoolean, boolean showReticleBoolean, boolean showZoomControlBoolean,
			boolean showFrameBoolean, String targetString, double zoomDouble, String projection, Widget widget) {
		this.aladinLiteDivName = divName;
		this.width = width;
		this.height = height;
		this.surveyString = surveyString;
		this.cooFrameString = cooFrameString;
		this.showLayersControlBoolean = showLayersControlBoolean;
		this.showGotoControlBoolean = showGotoControlBoolean;
		this.showFullscreenControlBoolean = showFullscreenControlBoolean;
		this.showShareControlBoolean = showShareControlBoolean;
		this.showReticleBoolean = showReticleBoolean;
		this.showZoomControlBoolean = showZoomControlBoolean;
		this.showFrameBoolean = showFrameBoolean;
		this.targetString = targetString;
		this.zoomDouble = zoomDouble;
		this.defaultProjection = projection != null && !projection.isEmpty() ? projection : this.defaultProjection;

		// Really bad practice, but see comment above...
		this.associatedWidget = widget;
		this.isAttached = false;
		initView();
	}

	/**
	 * Constructor used by ESASky
	 */
	public AladinLiteWidget(EventBus eventBus, String divName, double width, double height, String surveyId,
			String surveyName, String surveyRootUrl, String surveyFrame, int maximumNorder, String imgFormat,
			String cooFrameString, boolean showLayersControlBoolean, boolean showGotoControlBoolean,
			boolean showFullscreenControlBoolean, boolean showShareControlBoolean, boolean showReticleBoolean,
			boolean showZoomControlBoolean, boolean showFrameBoolean, String targetString, double zoomDouble, String projection,
			Widget widget) {
		AladinLiteWidget.eventBus = (SimpleEventBus) eventBus;
		this.aladinLiteDivName = divName;
		this.width = width;
		this.height = height;
		this.surveyString = surveyRootUrl;
		this.cooFrameString = cooFrameString;
		this.showLayersControlBoolean = showLayersControlBoolean;
		this.showGotoControlBoolean = showGotoControlBoolean;
		this.showFullscreenControlBoolean = showFullscreenControlBoolean;
		this.showShareControlBoolean = showShareControlBoolean;
		this.showReticleBoolean = showReticleBoolean;
		this.showZoomControlBoolean = showZoomControlBoolean;
		this.showFrameBoolean = showFrameBoolean;
		this.targetString = targetString;
		this.zoomDouble = zoomDouble;
		this.defaultProjection = projection != null && !projection.isEmpty() ? projection : this.defaultProjection;

		// Really bad practice, but see comment above...
		AladinLiteWidget.associatedWidget = widget;
		this.isAttached = false;

		initView();
	}

	/** Initialize view. */
	public final void initView() {

		this.aladinLiteGwtDiv = new HTML();

		this.aladinLiteGwtDiv.getElement().setId(this.aladinLiteDivName);
		this.aladinLiteGwtDiv.getElement().getStyle().setWidth(this.width, Unit.PX);
		this.aladinLiteGwtDiv.getElement().getStyle().setHeight(this.height, Unit.PX);

		initWidget(this.aladinLiteGwtDiv);
		exportFileDropHandler(this);
	}

	@Override
	public void onAttach() {

		super.onAttach();

		Log.debug("Into AladinLiteWidget.onAttach(" + this.aladinLiteDivName + ")");

		if (!this.isAttached) {
			this.aladinliteObj = initAladin(this.aladinLiteDivName, this.surveyString, this.cooFrameString,
					this.showLayersControlBoolean, this.showGotoControlBoolean, this.showFullscreenControlBoolean,
					this.showShareControlBoolean, this.showReticleBoolean, this.showZoomControlBoolean,
					this.showFrameBoolean, this.targetString, this.zoomDouble, this.defaultProjection);

			bind(this.aladinliteObj, this);
			customize(this.aladinliteObj, this);
			fireInitialEvents();
			this.isAttached = true;
		}
	}

	private void fireInitialEvents() {
		AladinLiteWidget.eventBus.fireEvent(new AladinLiteHasLoadedEvent());
		fireCoordinateFrameChangedEvent(getCooFrame());
		fireCoordinatesChangedEvent(getCenterLongitudeDeg(), getCenterLatitudeDeg(), true);

		double[] fov = getFov();
		fireFovChangedEvent(fov[0], fov[1]);
		fireProjectionChangedEvent(getCurrentProjection());
		deplayedViewChangeEventTimer = new DeplayedViewChangeEventTimer();
	}

	private native JavaScriptObject initAladin(String divNameString, String surveyString, String cooFrameString,
			boolean showLayersControlBoolean, boolean showGotoControlBoolean, boolean showFullscreenControlBoolean,
			boolean showShareControlBoolean, boolean showReticleBoolean, boolean showZoomControlBoolean,
			boolean showFrameBoolean, String targetString, double zoomDouble, String projection) /*-{
		var aladin = $wnd.A.aladin('#' + divNameString, {
			survey : surveyString,
			cooFrame : cooFrameString,
			showLayersControl : showLayersControlBoolean,
			showGotoControl : showGotoControlBoolean,
			showFullscreenControl : showFullscreenControlBoolean,
			showShareControl : showShareControlBoolean,
			showReticle : showReticleBoolean,
			showZoomControl : showZoomControlBoolean,
			showFrame : showFrameBoolean,
			target : targetString,
			zoom : zoomDouble,
			showProjectionControl: false,
			showFrame: false,
			gridColor: "rgb(120,255,200)",
			showContextMenu: false,
			showCooLocation: false,
			showFov: false,
			projection: projection,
			manualSelection: true,
			showStatusBar: false,
			selector : {
                color: function(startCoo, coo) {
                    if (coo && startCoo && (coo.x < startCoo.x)) {
						return "#f00e0e";
					}

					return "#78ffc8";
				}
			}
		});

		$wnd.aladin = aladin;
		return aladin;
	}-*/;

	private native void customize(JavaScriptObject aladinLiteJsObject, AladinLiteWidget instance) /*-{
		aladinLiteJsObject.wasm.createCustomColormap('planck', ['#0000ffff','#0001ffff','#0103ffff','#0204ffff','#0306ffff','#0307ffff','#0409ffff','#050affff','#060cffff','#060dffff','#070fffff','#0810ffff','#0912ffff','#0a14ffff','#0b20ffff','#0d2dffff','#0e39ffff','#1046ffff','#1153ffff','#135fffff','#146cffff','#1678ffff','#1785ffff','#1992ffff','#1a9effff','#1cabffff','#1eb8ffff','#21bbffff','#25bfffff','#29c3ffff','#2dc7ffff','#31cbffff','#35cfffff','#38d3ffff','#3cd7ffff','#40dbffff','#44dfffff','#48e3ffff','#4ce7ffff','#50ebffff','#58ebfeff','#61ebfeff','#69ebfdff','#72ecfdff','#7aecfdff','#83ecfcff','#8bedfcff','#94edfbff','#9cedfbff','#a5eefbff','#adeefaff','#b6eefaff','#bfeffaff','#c1eff9ff','#c4eff9ff','#c7eff8ff','#caeff8ff','#cdeff8ff','#d0eff7ff','#d2eff7ff','#d5eff6ff','#d8eff6ff','#dbeff6ff','#deeff5ff','#e1eff5ff','#e4f0f5ff','#e5f0f2ff','#e6f0efff','#e7f0ecff','#e8f0e9ff','#e9f0e6ff','#ebf0e3ff','#ecf0e0ff','#edf0ddff','#eef0daff','#eff0d7ff','#f1f1d4ff','#f1f1d4ff','#f1f0d0ff','#f1f0cdff','#f2f0c9ff','#f2f0c6ff','#f2f0c3ff','#f3f0bfff','#f3f0bcff','#f3f0b9ff','#f4f0b5ff','#f4f0b2ff','#f5f0afff','#f5efabff','#f5efa8ff','#f5eea4ff','#f5eea1ff','#f6ee9dff','#f6ed9aff','#f6ed96ff','#f6ec93ff','#f7ec8fff','#f7ec8cff','#f7eb88ff','#f7eb85ff','#f8eb82ff','#f8e87aff','#f8e673ff','#f8e36cff','#f8e165ff','#f8df5eff','#f8dc57ff','#f9da50ff','#f9d749ff','#f9d542ff','#f9d33bff','#f9d034ff','#f9ce2dff','#f9cc26ff','#f9c824ff','#f8c422ff','#f8c020ff','#f7bc1eff','#f6b81cff','#f6b41aff','#f5b018ff','#f5ac16ff','#f4a814ff','#f4a412ff','#f3a010ff','#f29c0eff','#f2990cff','#ef930bff','#ec8d0aff','#e98709ff','#e68108ff','#e37b07ff','#e07506ff','#dd6f05ff','#da6904ff','#d76403ff','#d45e02ff','#d15801ff','#ce5200ff','#cc4c00ff','#c94902ff','#c64504ff','#c34207ff','#c03e09ff','#bd3b0cff','#ba370eff','#b73411ff','#b43113ff','#b12d16ff','#ae2a18ff','#ab261bff','#a8231dff','#a52020ff','#a11d20ff','#9d1b20ff','#991820ff','#951620ff','#911320ff','#8d1120ff','#890e20ff','#850c20ff','#810920ff','#7d0720ff','#790420ff','#750220ff','#720020ff','#730929ff','#741332ff','#751d3bff','#762745ff','#77314eff','#783a57ff','#794461ff','#7a4e6aff','#7b5873ff','#7c627dff','#7d6b86ff','#7e758fff','#7f7f99ff','#83839cff','#8787a0ff','#8b8ba4ff','#8f8fa8ff','#9393acff','#9797b0ff','#9a9ab4ff','#9e9eb8ff','#a2a2bcff','#a6a6c0ff','#aaaac4ff','#aeaec8ff','#b2b2ccff','#b4b4cdff','#b6b6cfff','#b8b8d1ff','#babad3ff','#bcbcd5ff','#bebed7ff','#c0c0d9ff','#c2c2dbff','#c4c4ddff','#c6c6dfff','#c8c8e1ff','#cacae3ff','#cccce5ff','#cdcde6ff','#cfcfe7ff','#d1d1e8ff','#d3d3e9ff','#d5d5eaff','#d7d7ebff','#d9d9ecff','#dbdbedff','#ddddeeff','#dfdfefff','#e1e1f0ff','#e3e3f1ff','#e5e5f2ff','#e6e6f2ff','#e7e7f3ff','#e8e8f4ff','#e9e9f4ff','#eaeaf5ff','#ebebf5ff','#ececf6ff','#ededf6ff','#eeeef7ff','#efeff8ff','#f0f0f8ff','#f1f1f9ff','#f2f2f9ff','#f2f2faff','#f3f3faff','#f3f3faff','#f3f3faff','#f4f4faff','#f4f4fbff','#f4f4fbff','#f5f5fbff','#f5f5fbff','#f6f6fbff','#f6f6fcff','#f6f6fcff','#f7f7fcff','#f7f7fcff','#f8f8fcff','#f8f8fdff','#f9f9fdff','#f9f9fdff','#fafafdff','#fafafeff','#fbfbfeff','#fbfbfeff','#fbfbfeff','#fcfcffff']);
		$wnd.increaseBrightness = function(color, amount) {
			return @esac.archive.absi.modules.cl.aladinlite.widget.client.AladinLiteUtils::increaseBrightness(*)(color, amount);
		}
}-*/;
	private native void bind(JavaScriptObject aladinLiteJsObject, AladinLiteWidget instance)/*-{

		// define function triggered when an object is hovered
		aladinLiteJsObject
				.on(
						'objectHovered',
						function(object) {
							if (object) {
								var overlayOrCatalogName = object.overlay ? object.overlay.name
										: object.catalog.name;
                                if (object.id !== undefined  && !overlayOrCatalogName.endsWith("moc_overlay")) {
									instance.@esac.archive.absi.modules.cl.aladinlite.widget.client.AladinLiteWidget::fireShapeHoverStart(*)(object.id, overlayOrCatalogName, object);
								}
							}
						});

		aladinLiteJsObject
				.on(
						'objectHoveredStop',
						function(object) {
							var overlayOrCatalogName = object.overlay ? object.overlay.name
									: object.catalog.name;
							if (object.id !== undefined && !overlayOrCatalogName.endsWith("moc_overlay")) {
                                instance.@esac.archive.absi.modules.cl.aladinlite.widget.client.AladinLiteWidget::fireShapeHoverStop(*)(object.id, overlayOrCatalogName, object);
                            }

						});
		aladinLiteJsObject
				.on(
					"objectClicked",
					function(o, xy) {
                        if (o && o.catalog) {
							if (o.isSelected) {
								instance.@esac.archive.absi.modules.cl.aladinlite.widget.client.AladinLiteWidget::fireShapeDeselection(*)(o.data.id,o.data.catalogue, o);
							} else {
								instance.@esac.archive.absi.modules.cl.aladinlite.widget.client.AladinLiteWidget::fireShapeSelection(*)(o.data.id,o.data.catalogue, o);
							}
						}
				});

		aladinLiteJsObject
				.on(
						'select',
						function(object) {
							instance.@esac.archive.absi.modules.cl.aladinlite.widget.client.AladinLiteWidget::fireSelectEvent(Lesac/archive/absi/modules/cl/aladinlite/widget/client/model/AladinShape;)(object);
						});

		aladinLiteJsObject
				.on(
						'selectArea',
						function(objects, area, isSelection) {
							if (isSelection) {
								instance.@esac.archive.absi.modules.cl.aladinlite.widget.client.AladinLiteWidget::fireSelectAreaEvent(Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;)(objects, area);
							} else {
								instance.@esac.archive.absi.modules.cl.aladinlite.widget.client.AladinLiteWidget::fireDeselectAreaEvent(Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;)(objects, area);
							}
						});
		aladinLiteJsObject
				.on(
						'selectSearchArea',
						function(searchArea) {
							instance.@esac.archive.absi.modules.cl.aladinlite.widget.client.AladinLiteWidget::fireSelectSearchAreaEvent(Lesac/archive/absi/modules/cl/aladinlite/widget/client/model/SearchArea;)(searchArea);
						});
		aladinLiteJsObject
				.on(
						'clearSearchArea',
						function() {
							instance.@esac.archive.absi.modules.cl.aladinlite.widget.client.AladinLiteWidget::fireClearSearchAreaEvent()();
						});
		aladinLiteJsObject
				.on(
						'zoomChanged',
						function(fov) {
							var fov = instance.@esac.archive.absi.modules.cl.aladinlite.widget.client.AladinLiteWidget::getFov()();
							instance.@esac.archive.absi.modules.cl.aladinlite.widget.client.AladinLiteWidget::fireFovChangedEvent(DD)(fov[0], fov[1]);
						});
		aladinLiteJsObject
				.on(
						'positionChanged',
						function(position) {
							instance.@esac.archive.absi.modules.cl.aladinlite.widget.client.AladinLiteWidget::fireCoordinatesChangedEvent(DDZ)(position.ra, position.dec, position.dragging);
						});
		aladinLiteJsObject
				.on(
						'mouseMove',
						function(position) {
							instance.@esac.archive.absi.modules.cl.aladinlite.widget.client.AladinLiteWidget::fireCoordinatesChangedEvent(DDZ)(position.ra, position.dec, false);
						});
		aladinLiteJsObject
				.on(
						'cooFrameChanged',
						function(newFrame) {
							instance.@esac.archive.absi.modules.cl.aladinlite.widget.client.AladinLiteWidget::fireCoordinateFrameChangedEvent(Ljava/lang/String;)(newFrame);
						});
		aladinLiteJsObject
				.on(
						'cooFrameChanged',
						function(newFrame) {
							instance.@esac.archive.absi.modules.cl.aladinlite.widget.client.AladinLiteWidget::fireCoordinateFrameChangedEvent(Ljava/lang/String;)(newFrame);
						});

		aladinLiteJsObject
				.on(
						'rightClickMove',
						function(x, y) {
							// Add this listener to disable the default aladinlite contrast adjustment
						});



//		aladinLiteJsObject
//				.fire(
//						'selectstart',
//						function(object) {
//							instance.@esac.archive.absi.modules.cl.aladinlite.widget.client.AladinLiteWidget::fireSelectStartEvent(Lesac/archive/absi/modules/cl/aladinlite/widget/client/model/AladinShape;)(object);
//						});
//
//		aladinLiteJsObject
//				.fire(
//						'selectend',
//						function(object) {
//							instance.@esac.archive.absi.modules.cl.aladinlite.widget.client.AladinLiteWidget::fireSelectEndEvent(Lesac/archive/absi/modules/cl/aladinlite/widget/client/model/AladinShape;)(object);
//						});

		aladinLiteJsObject
				.on(
						'mocPixClicked',
						function(object, x, y) {
							instance.@esac.archive.absi.modules.cl.aladinlite.widget.client.AladinLiteWidget::fireMOCIpixClickedEvent(Lcom/google/gwt/core/client/JavaScriptObject;II)(object, x, y);
						});
		aladinLiteJsObject
				.on(
						'onLongTouch',
						function(e) {
							instance.@esac.archive.absi.modules.cl.aladinlite.widget.client.AladinLiteWidget::fireOpenContextMenuEvent(*)(e);
						});

		$wnd
				.addEventListener(
						"footprintClicked",
						function(e) {
							var object = e.detail.shape;
							if (object.isSelected) {
								instance.@esac.archive.absi.modules.cl.aladinlite.widget.client.AladinLiteWidget::fireShapeDeselection(*)(object.id,object.overlay.name, object);
							} else {
								instance.@esac.archive.absi.modules.cl.aladinlite.widget.client.AladinLiteWidget::fireShapeSelection(*)(object.id,object.overlay.name,object);
							}
						});

		$wnd
				.addEventListener(
						"shapeSelected",
						function(e) {
							instance.@esac.archive.absi.modules.cl.aladinlite.widget.client.AladinLiteWidget::fireShapeSelection(*)(e.detail.id,e.detail.overlayName,e.detail.shapeobj);
						});

		$wnd
				.addEventListener(
						"shapeDeselected",
						function(e) {
							instance.@esac.archive.absi.modules.cl.aladinlite.widget.client.AladinLiteWidget::fireShapeDeselection(*)(e.detail.id,e.detail.overlayName,e.detail.shapeobj);
						});
		aladinLiteJsObject.aladinDiv.addEventListener(
            "AL:projection.changed",
				function (e) {
					instance.@esac.archive.absi.modules.cl.aladinlite.widget.client.AladinLiteWidget::fireProjectionChangedEvent(*)(e.detail.projection);
				}
		);

		aladinLiteJsObject.aladinDiv.addEventListener(
				"AL:Event",
				function (e) {
                    if (e.detail.type === "mouseout") {
                        var center = aladinLiteJsObject.view.viewCenter;
						instance.@esac.archive.absi.modules.cl.aladinlite.widget.client.AladinLiteWidget::fireCoordinatesChangedEvent(DDZ)(center.lon, center.lat, true);
					} else {
						var rightClickDurationMs = Date.now() - aladinLiteJsObject.view.rightClickTimeStart;
						if (aladinLiteJsObject.view.rightClick && rightClickDurationMs < 300) {
							var evt = new MouseEvent('contextmenu',{bubbles:true, clientX: e.detail.xy.x, clientY: e.detail.xy.y});
							e.target.dispatchEvent(evt);
						}
					}
				}
		);
		aladinLiteJsObject.aladinDiv.addEventListener(
				"AL:HiPSLayer.added",
				function (e) {
					var layer = e.detail.layer;
                    if (layer && layer.imgFormat === "fits") {
						instance.@esac.archive.absi.modules.cl.aladinlite.widget.client.AladinLiteWidget::fireFitsImageAdded(*)(layer);
					}
				}
		);

        aladinLiteJsObject.aladinDiv.addEventListener(
            "AL:HiPSLayer.changed",
			function (e) {
				var layer = e.detail.layer;
				if (layer) {
					instance.@esac.archive.absi.modules.cl.aladinlite.widget.client.AladinLiteWidget::fireHipsLayerChanged(*)(layer);
				}
			}
		)
	}-*/;

	private void fireFovChangedEvent(double fovRa, double fovDec) {
		AladinLiteWidget.eventBus.fireEvent(new AladinLiteFoVChangedEvent(fovRa, fovDec));
	}

	private void fireCoordinatesChangedEvent(double ra, double dec, boolean isViewCenterPosition) {
		AladinLiteWidget.eventBus
				.fireEvent(new AladinLiteCoordinatesChangedEvent(ra, dec, Boolean.valueOf(isViewCenterPosition)));
	}

	private void fireCoordinateFrameChangedEvent(String coordinateFrame) {
		AladinLiteWidget.eventBus
				.fireEvent(new AladinLiteCoordinateFrameChangedEvent(CoordinateFrame.fromString(coordinateFrame)));
	}

	private void fireMOCIpixClickedEvent(JavaScriptObject text, int x, int y) {
		AladinLiteWidget.eventBus.fireEvent(new AladinLiteMOCIpixClickedEvent(text, x, y));
	}

	private void fireOpenContextMenuEvent(Event event) {
		AladinLiteWidget.eventBus.fireEvent(new AladinOpenContextMenuEvent(event));
	}

	private class DeplayedViewChangeEventTimer extends Timer {

		private int timeoutInMillis = 1000;
		private double fovAtLatestEvent = getFovDeg();
		private double latAtLatestEvent = getCenterLatitudeDeg();
		private double lonAtLatestEvent = getCenterLongitudeDeg();

		public DeplayedViewChangeEventTimer() {
			fovAtLatestEvent = getFovDeg();
			latAtLatestEvent = getCenterLatitudeDeg();
			lonAtLatestEvent = getCenterLongitudeDeg();

			AladinLiteWidget.eventBus.addHandler(AladinLiteFoVChangedEvent.TYPE,
					new AladinLiteFoVChangedEventHandler() {

						@Override
						public void onChangeEvent(AladinLiteFoVChangedEvent changeEvent) {
							skyViewChanged();
						}
					});
			AladinLiteWidget.eventBus.addHandler(AladinLiteCoordinatesChangedEvent.TYPE,
					new AladinLiteCoordinatesChangedEventHandler() {

						@Override
						public void onCoordsChanged(AladinLiteCoordinatesChangedEvent coordinateEvent) {
							if (coordinateEvent.getIsViewCenterPosition()) {
								skyViewChanged();
							}
						}
					});
			AladinLiteWidget.eventBus.addHandler(AladinLiteCoordinateFrameChangedEvent.TYPE,
					new AladinLiteCoordinateFrameChangedEventHandler() {

						@Override
						public void onFrameChanged(AladinLiteCoordinateFrameChangedEvent frameChangeEvent) {
							skyViewChanged();
						}
					});
		}

		@Override
		public void run() {
			if (Math.abs(fovAtLatestEvent - getFovDeg()) > fovAtLatestEvent / 1000
					|| Math.abs(latAtLatestEvent - getCenterLatitudeDeg()) > 1e-4
					|| Math.abs(lonAtLatestEvent - getCenterLongitudeDeg()) > 1e-4) {
				fovAtLatestEvent = getFovDeg();
				latAtLatestEvent = getCenterLatitudeDeg();
				lonAtLatestEvent = getCenterLongitudeDeg();
				fireCoordinatesOrFoVChangedEvent();
			}
		}

		private void skyViewChanged() {
			schedule(timeoutInMillis);
		}

	}

	private DeplayedViewChangeEventTimer deplayedViewChangeEventTimer;

	public void setCooFrame(CoordinateFrame frame) {
		setCooFrame(this.aladinliteObj, frame.toString());
	}

	private native void setCooFrame(JavaScriptObject aladinLiteJsObject, String frame)/*-{
		aladinLiteJsObject.setFrame(frame);
	}-*/;

	private void fireShapeSelection(int shapeId, String overlayName, AladinShape shapeobj) {
		Log.debug("[AladinLiteWidget][fireShapeSelection]");
		if (AladinLiteWidget.eventBus != null) {
			AladinLiteWidget.eventBus.fireEvent(new AladinLiteShapeSelectedEvent(shapeId, overlayName, shapeobj));
		}
	}

	private void fireShapeDeselection(int shapeId, String overlayName, AladinShape shapeobj) {
		Log.debug("[AladinLiteWidget][fireShapeDeselection]");
		if (AladinLiteWidget.eventBus != null) {
			AladinLiteWidget.eventBus.fireEvent(new AladinLiteShapeDeselectedEvent(shapeId, overlayName, shapeobj));
		}
	}

	private void fireShapeHoverStart(int shapeId, String overlayName, AladinShape shapeobj) {
		if (AladinLiteWidget.eventBus != null) {
			AladinLiteWidget.eventBus.fireEvent(new AladinLiteShapeHoverStartEvent(shapeId, overlayName, shapeobj));
		}
	}

	private void fireShapeHoverStop(int shapeId, String overlayName, AladinShape shapeobj) {
		if (AladinLiteWidget.eventBus != null) {
			AladinLiteWidget.eventBus.fireEvent(new AladinLiteShapeHoverStopEvent(shapeId, overlayName, shapeobj));
		}
	}

	private void fireCoordinatesOrFoVChangedEvent() {
		if (AladinLiteWidget.eventBus != null) {
			AladinLiteWidget.eventBus.fireEvent(new AladinLiteCoordinatesOrFoVChangedEvent());
		}
	}

	private void fireProjectionChangedEvent(String projection) {
		if (AladinLiteWidget.eventBus != null) {
			AladinLiteWidget.eventBus.fireEvent(new AladinLiteProjectionChangedEvent(projection));
		}
	}

	private void fireFitsImageAdded(ImageLayer imageLayer) {
		if (AladinLiteWidget.eventBus != null) {
			AladinLiteWidget.eventBus.fireEvent(new AladinLiteFitsImageAddedEvent(imageLayer));
		}
	}

	private void fireHipsLayerChanged(ImageLayer imageLayer) {
		if (AladinLiteWidget.eventBus != null) {
			AladinLiteWidget.eventBus.fireEvent(new HipsLayerChangedEvent(imageLayer));
		}
	}

	private void fireHoverEvent(AladinShape object) {
		associatedWidget.fireEvent(new AladinLiteHoverEvent(object));
	}

	private void fireSelectEvent(AladinShape object) {
		associatedWidget.fireEvent(new AladinLiteSelectEvent(object));
	}

	private void fireSelectAreaEvent(JavaScriptObject objects, JavaScriptObject area) {
//        Log.debug("[AladinLiteWidget][fireSelectionArea]");
		if (AladinLiteWidget.eventBus != null) {
			AladinLiteWidget.eventBus.fireEvent(new AladinLiteSelectAreaEvent(objects, area));
		}
	}

	private void fireSelectSearchAreaEvent(SearchArea searchArea) {
		if (AladinLiteWidget.eventBus != null) {
			AladinLiteWidget.eventBus.fireEvent(new AladinLiteSelectSearchAreaEvent(searchArea));
		}
	}

	private void fireClearSearchAreaEvent() {
		if (AladinLiteWidget.eventBus != null) {
			AladinLiteWidget.eventBus.fireEvent(new AladinLiteClearSearchAreaEvent());
		}
	}

	private void fireDeselectAreaEvent(JavaScriptObject objects, JavaScriptObject area) {
//    	 Log.debug("[AladinLiteWidget][fireDeselectionArea]");
		if (AladinLiteWidget.eventBus != null) {
			AladinLiteWidget.eventBus.fireEvent(new AladinLiteDeselectAreaEvent(objects, area));
		}
	}

	private void fireConfirmDialogRequested(String title, String message, String help, JavaScriptObject callback) {
		if (AladinLiteWidget.eventBus != null) {
			AladinLiteWidget.eventBus.fireEvent(new AladinConfirmDialogRequestedEvent(title, message, help, callback));
		}
	}

	private void fireSelectStartEvent(AladinShape object) {
		associatedWidget.fireEvent(new AladinLiteSelectStartEvent(object));
	}

	private void fireSelectEndEvent(AladinShape object) {
		associatedWidget.fireEvent(new AladinLiteSelectEndEvent(object));
	}

	public CoordinatesObject convertMouseXYToRaDecDeg(double mouseX, double mouseY) {
		return cds_convertMouseXYToRaDecDeg(this.aladinliteObj, mouseX, mouseY);
	}

	private native CoordinatesObject cds_convertMouseXYToRaDecDeg(JavaScriptObject aladinLiteJsObject, double mouseX,
			double mouseY)/*-{
		var xy = aladinLiteJsObject.pix2world(mouseX, mouseY);
		return {ra: xy[0], dec: xy[1], mousex: mouseX, mousey: mouseY};
	}-*/;

	public CoordinatesObject convertRaDecDegToMouseXY(double raDeg, double decDeg) {
		return cds_convertRaDecDegToMouseXY(this.aladinliteObj, raDeg, decDeg);
	}

	private native CoordinatesObject cds_convertRaDecDegToMouseXY(JavaScriptObject aladinLiteJsObject, double raDeg,
			double decDeg)/*-{
		var xy = aladinLiteJsObject.world2pix(raDeg, decDeg);

        if (!xy) {
			return null; // Projection not possible
		}
		return {
			mousex : xy[0],
			mousey : xy[1]
		};
	}-*/;

	public void removeAllSurveys() {
		cds_removeAllSurveys(this.aladinliteObj);
	}

	private native void cds_removeAllSurveys(JavaScriptObject aladinLiteJsObject) /*-{
		aladinLiteJsObject.removeAllSurveys();
	}-*/;

	public void removeSurvey(String surveyIdToRemove) {
		cds_removeSurvey(this.aladinliteObj, surveyIdToRemove);
	}

	private native void cds_removeSurvey(JavaScriptObject aladinLiteJsObject, String surveyIdToRemove) /*-{
		aladinLiteJsObject.removeSurvey(surveyIdToRemove);
	}-*/;

	@Override
	public void goToObject(String inputObjectName) {
		cds_goToObject(this.aladinliteObj, inputObjectName);
	}

	private native void cds_goToObject(JavaScriptObject aladinLiteJsObject, String inputObjectName) /*-{
		aladinLiteJsObject.gotoObject(inputObjectName);
	}-*/;

	public void goToRaDec(String ra, String dec) {
		goToRaDec(this.aladinliteObj, ra, dec);
	}

	private native void goToRaDec(JavaScriptObject aladinLiteJsObject, String ra, String dec) /*-{
		aladinLiteJsObject.gotoRaDec(ra, dec);
	}-*/;

	public void increaseZoom() {
		increaseZoom(this.aladinliteObj);
	}

	private native void increaseZoom(JavaScriptObject aladinLiteJsObject) /*-{
		aladinLiteJsObject.increaseZoom();
	}-*/;

	public void decreaseZoom() {
		decreaseZoom(this.aladinliteObj);
	}

	private native void decreaseZoom(JavaScriptObject aladinLiteJsObject) /*-{
		aladinLiteJsObject.decreaseZoom();
	}-*/;

	public void increaseZoom(double step) {
		increaseZoom(this.aladinliteObj, step);
	}

	private native void increaseZoom(JavaScriptObject aladinLiteJsObject, double step) /*-{
		aladinLiteJsObject.increaseZoom(step);
	}-*/;

	public void decreaseZoom(double step) {
		decreaseZoom(this.aladinliteObj, step);
	}

	private native void decreaseZoom(JavaScriptObject aladinLiteJsObject, double step) /*-{
		aladinLiteJsObject.decreaseZoom(step);
	}-*/;

	public String getCooFrame() {
		return CoordinateFrame.fromString(cds_getCooFrame(this.aladinliteObj)).name();
	}

	private native String cds_getCooFrame(JavaScriptObject aladinLiteJsObject) /*-{
		return aladinLiteJsObject.view.cooFrame.system;
	}-*/;


	public String getCurrentProjection() {
		return getCurrentProjection(this.aladinliteObj);
	}

	private native String getCurrentProjection(JavaScriptObject aladinLiteJsObject) /*-{
		return aladinLiteJsObject.getProjectionName();
	}-*/;


	public void setProjection(String projectionName) {
		if (isAttached) {
			setProjection(this.aladinliteObj, projectionName);
		} else {
			defaultProjection = projectionName;
		}
	}

	private native void setProjection(JavaScriptObject aladinLiteJsObject, String projectionName) /*-{
		aladinLiteJsObject.setProjection(projectionName);
	}-*/;

	/*****************************************************/
	/***************
	 * Overlay methods ********************* /
	 *****************************************************/


	public void removeOverlay(JavaScriptObject overlayJsObject) {
		removeOverlay(this.aladinliteObj, overlayJsObject);
	}

	private native void removeOverlay(JavaScriptObject aladinLiteJsObject, JavaScriptObject overlayJsObject) /*-{
		aladinLiteJsObject.removeOverlay(overlayJsObject);
	}-*/;

	public void removeOverlay(String layerId) {
		removeOverlay(this.aladinliteObj, layerId);
	}

	private native void removeOverlay(JavaScriptObject aladinLiteJsObject, String layerId) /*-{
		aladinLiteJsObject.removeOverlay(layerId);

	}-*/;

	private native JavaScriptObject cds_createJ2000Circle(double ra, double dec, double radius, String tgtColor)/*-{
		return $wnd.A.circle(ra, dec, radius, {
			color : tgtColor
		});
	}-*/;

	private native void cds_addJ2000CircleToOverlay(JavaScriptObject overlayJsObject, JavaScriptObject tgtcircle)/*-{
		overlayJsObject.add(tgtcircle);
	}-*/;

	@Override
	public void addJ2000PolylineToOverlay(JavaScriptObject overlayJsObject, JavaScriptObject polyline) {
		cds_addJsJ2000PolylineToOverlay(overlayJsObject, polyline);
	}

	@Override
	public void removeJ2000PolylineFromOverlay(JavaScriptObject overlayJsObject, JavaScriptObject polyline) {
		cds_removeJsJ2000PolylineFromOverlay(overlayJsObject, polyline);
	}

	@Override
	public JavaScriptObject createJ2000Polyline(double[] polylineDouble) {
		JsArray<JavaScriptObject> polylines = cds_getNativeJsArray();
		JsArray<JavaScriptObject> mypolylinepoints = cds_getNativeJsArray();
		JavaScriptObject mypoint = cds_getNativeJsArray();

		double ra;
		double dec;
		for (int j = 0; j < polylineDouble.length; j = j + 2) {
			ra = polylineDouble[j];
			dec = polylineDouble[j + 1];
			mypoint = cds_getRaDecArray(ra, dec);
			mypolylinepoints.push(mypoint);

			// Once we have 4 points, create a polyline and reset for the next segment
			if (mypolylinepoints.length() == 4) {

				// Connect to next point
				if (polylineDouble.length > j + 3) {
					ra = polylineDouble[j+2];
					dec = polylineDouble[j + 3];
					mypoint = cds_getRaDecArray(ra, dec);
					mypolylinepoints.push(mypoint);
				}


				JavaScriptObject polyline = cds_createJ2000Polyline(mypolylinepoints);
				polylines.push(polyline);
				mypolylinepoints = cds_getNativeJsArray(); // Reset for the next 4 points
			}
		}

		// If any remaining points (less than 4) are left, add them as the final polyline
		if (mypolylinepoints.length() > 0) {
			JavaScriptObject polyline = cds_createJ2000Polyline(mypolylinepoints);
			polylines.push(polyline);
		}

		return polylines;
	}

	private native JavaScriptObject cds_createJ2000Polyline(JsArray<JavaScriptObject> tgtpolyline)/*-{
		return $wnd.A.polyline(tgtpolyline, {closed: false});
	}-*/;

	private native void cds_addJsJ2000PolylineToOverlay(JavaScriptObject overlayJsObject, JavaScriptObject tgtpolyline)/*-{
    	for (var i = 0; i < tgtpolyline.length; i++ ) {
			overlayJsObject.add(tgtpolyline[i]);
		}
	}-*/;

	private native void cds_removeJsJ2000PolylineFromOverlay(JavaScriptObject overlayJsObject,
			JavaScriptObject tgtpolyline)/*-{
		overlayJsObject.remove(tgtpolyline);
	}-*/;


	private native JavaScriptObject cds_createJ2000Polygon(JsArray<JavaScriptObject> tgtpolygon)/*-{
		return $wnd.A.polygon(tgtpolygon);
	}-*/;

	private native JavaScriptObject cds_createJ2000Polygon(JsArray<JavaScriptObject> tgtpolygon, String obsid)/*-{
		return $wnd.A.polygon(tgtpolygon, obsid);
	}-*/;

	private native void cds_addJsJ2000PolygonsToOverlay(JavaScriptObject overlayJsObject,
			JsArray<JavaScriptObject> tgtpolygon)/*-{
		overlayJsObject.addFootprints(tgtpolygon);
	}-*/;


	private native JavaScriptObject cds_getRaDecArray(double ra, double dec)/*-{
		var point = new Array();
		point[0] = ra;
		point[1] = dec;
		return point;
	}-*/;


	private native JavaScriptObject cds_createCatalog(String catalogname, int tgtsourcesize, String tgtcolor,
			String tgtHoverColor)/*-{
		return $wnd.A.catalog({
			name : catalogname,
			sourceSize : tgtsourcesize,
			color : tgtcolor,
			hoverColor : tgtHoverColor
		});
	}-*/;


	@Override
	public void addCatalogToAladin(JavaScriptObject catalog) {
		cds_addCatalogToAladin(this.aladinliteObj, catalog);
	}

	private native void cds_addCatalogToAladin(JavaScriptObject aladinLiteObj, JavaScriptObject catalog)/*-{
		aladinLiteObj.addCatalog(catalog);
	}-*/;

	private native void cds_addSourcesToCatalog(JavaScriptObject catalog, JsArray<JavaScriptObject> markers)/*-{
		catalog.addSources(markers);
	}-*/;

	public void removeCatalog(JavaScriptObject catalog) {
		removeOverlay(this.aladinliteObj, catalog);
	}

	private native void removeCatalog(JavaScriptObject aladinLiteJsObject, JavaScriptObject catalog) /*-{
		aladinLiteJsObject.removeOverlay(catalog);
	}-*/;


	private native JavaScriptObject cds_createJ2000Marker(double ra, double dec, String popupHTMLTitle,
			String popupHTMLDesc)/*-{
		return $wnd.A.marker(ra, dec, {
			popupTitle : popupHTMLTitle,
			popupDesc : popupHTMLDesc
		});
	}-*/;

	@Override
	public void displayJPG(String imageURL, String transparency) {
		cds_displayJPG(this.aladinliteObj, imageURL, transparency);
	}

	public void displayJPG(String imageURL, String transparency, double fov) {
		cds_displayJPG(this.aladinliteObj, imageURL, transparency, fov);
	}

	private native void cds_displayJPG(JavaScriptObject aladinLiteJsObject, String imageURL, String tgttransparency,
			double fov) /*-{
		aladinLiteJsObject.displayJPG(imageURL, aladinLiteJsObject, {
			transparency : tgttransparency
		}, fov);
	}-*/;

	private native void cds_displayJPG(JavaScriptObject aladinLiteJsObject, String imageURL, String tgttransparency) /*-{
		aladinLiteJsObject.displayJPG(imageURL, aladinLiteJsObject, {
			transparency : tgttransparency
		});
	}-*/;

	/*****************************************************/
	/*************** Image Survey methods ****************/
	/*****************************************************/

	public void setImageSurvey(String surveyIdentifier) {
		setImageSurvey(this.aladinliteObj, surveyIdentifier);
	}

	private native void setImageSurvey(JavaScriptObject aladinLiteJsObject, String surveyIdentifier) /*-{
		aladinLiteJsObject.setImageSurvey(surveyIdentifier);
	}-*/;


	@Override
	public JavaScriptObject createAndSetImageSurveyWithImgFormat(String skyRowId, String surveyId, String surveyName,
			String surveyRootUrl, String surveyFrame, int maximumNorder, String imgFormat, String colormap, boolean shouldUseCredentials) {
		lastCreatedSurveyJSObj = cds_createAndSetImageSurveyWithImgFormat(this.aladinliteObj, skyRowId, surveyId, surveyName,
				surveyRootUrl, surveyFrame, maximumNorder, imgFormat, colormap, shouldUseCredentials);
		return lastCreatedSurveyJSObj;
	}

	@Override
	public JavaScriptObject getCurrentImageSurveyObject() {
		return lastCreatedSurveyJSObj;
	}

	public ImageLayer getImageLayer(String layer) {
		return getImageLayer(this.aladinliteObj, layer);
	}

	private native ImageLayer getImageLayer(JavaScriptObject aladinLiteJsObject, String layer) /*-{
		return aladinLiteJsObject.getOverlayImageLayer(layer);
	}-*/;

	public Map<String, ImageLayer> getImageLayers() {
		return getImageLayers(this.aladinliteObj);
	}

	private native Map<String, ImageLayer> getImageLayers(JavaScriptObject aladinLiteJsObject) /*-{
		return aladinLiteJsObject.view.imageLayers;
	}-*/;

	public void renameImageLayer(String layerId, String newLayerId) {
		renameImageLayer(aladinliteObj, layerId, newLayerId);
	}

	private native void renameImageLayer(JavaScriptObject aladinLiteJsObject, String layerId, String newLayerId) /*-{
    	var layer = aladinLiteJsObject.view.getImageLayer(layerId);
    	if (layer) {
			aladinLiteJsObject.view.renameLayer(layerId, newLayerId);
		}
	}-*/;

	public ImageLayer add(String layer) {
		return getImageLayer(this.aladinliteObj, layer);
	}

	public int getNumberOfImageLayers() {
		return getNumberOfImageLayers(aladinliteObj);
	}

	private native int getNumberOfImageLayers(JavaScriptObject aladinLiteJsObject) /*-{
		return aladinLiteJsObject.view.imageLayers.length();
	}-*/;

	private native JavaScriptObject cds_createAndSetImageSurveyWithImgFormat(JavaScriptObject aladinLiteJsObject,
			String skyRowId, String surveyId, String surveyName, String surveyRootUrl, String surveyFrame, int maximumNorder,
			String imgFormat, String colormap, boolean shouldUseCredentials) /*-{

		var imageSurvey;
    	if (imgFormat === "fits") {
			imageSurvey = aladinLiteJsObject.createImageFITS(surveyRootUrl, {name: surveyName}, function(ra, dec, fov) {
				// Center the view around the new fits object
				aladin.gotoRaDec(ra, dec);
				aladin.setFoV(fov * 1.1);
			});
		} else {
			imageSurvey = aladinLiteJsObject.createImageSurvey(surveyId,
					surveyName, surveyRootUrl, surveyFrame, maximumNorder, {
						imgFormat : imgFormat,
						colormap: colormap,
						requestCredentials: (shouldUseCredentials ? 'include' : 'omit'),
						requestMode: 'cors'
					});
		}

		aladinLiteJsObject.setOverlayImageLayer(imageSurvey, skyRowId);
		return imageSurvey;
	}-*/;

	public JavaScriptObject createAndSetLocalImageSurvey(String skyRowId, String surveyId, String surveyName, String surveyRootUrl,
			String surveyFrame, int maximumNorder, String imgFormat, JavaScriptObject files, String colormap) {
		lastCreatedSurveyJSObj = cds_createAndSetLocalImageSurvey(this.aladinliteObj, skyRowId, surveyId, surveyName,
				surveyRootUrl, surveyFrame, maximumNorder, imgFormat, files, colormap);
		return lastCreatedSurveyJSObj;
	}

	private native JavaScriptObject cds_createAndSetLocalImageSurvey(JavaScriptObject aladinLiteJsObject,
			String skyRowId, String surveyId, String surveyName, String surveyRootUrl, String surveyFrame, int maximumNorder,
			String imgFormat, JavaScriptObject files, String colormap) /*-{

		var imageSurvey = aladinLiteJsObject.createImageSurvey(surveyId,
				surveyName, files, surveyFrame, maximumNorder, {
					imgFormat : imgFormat,
					colormap: colormap
				});

		aladinLiteJsObject.setOverlayImageLayer(imageSurvey, skyRowId);
		return imageSurvey;
	}-*/;

	public void setImageLayerOpacity(String layerId, double alpha) {
		setImageLayerOpacity(this.aladinliteObj, layerId, alpha);
	}

	private native void setImageLayerOpacity(JavaScriptObject aladinLiteJsObject, String layerId, double alpha)/*-{
		var layer = aladinLiteJsObject.getOverlayImageLayer(layerId);
		layer.setOpacity(alpha);
	}-*/;

	public void toggleImageLayer(String layerId) {
		toggleImageLayer(this.aladinliteObj, layerId);
	}

	private native void toggleImageLayer(JavaScriptObject aladinLiteJsObject, String layerId)/*-{
		var layer = aladinLiteJsObject.getOverlayImageLayer(layerId);
		layer.toggle();
	}-*/;

	public void focusImageLayer(String layerId) {
		focusImageLayer(this.aladinliteObj, layerId);
	}

	private native void focusImageLayer(JavaScriptObject aladinLiteJsObject, String layerId)/*-{
		var imageLayers = aladinLiteJsObject.view.imageLayers;

		if (imageLayers && imageLayers.size > 0) {
            var mapEntries = imageLayers.entries();
            var entry = mapEntries.next();

            while (!entry.done) {
                var key = entry.value[0];
                var value = entry.value[1];

				if (layerId !== key && value.getOpacity() > 0.0) {
					value.toggle();
				}

                entry = mapEntries.next();
            }
        }

		var layer = aladinLiteJsObject.getOverlayImageLayer(layerId);
		if (layer) {
			layer.setOpacity(1);
		}

	}-*/;

	public void restoreImageLayersFocus() {
		restoreImageLayersFocus(this.aladinliteObj);
	}

	private native void restoreImageLayersFocus(JavaScriptObject aladinLiteJsObject)/*-{
		var imageLayers = aladinLiteJsObject.view.imageLayers;

		if (imageLayers && imageLayers.size > 0) {
			var mapEntries = imageLayers.entries();
			var entry = mapEntries.next();

			while (!entry.done) {
				var value = entry.value[1];

				if (value.getOpacity() == 0.0) {
					value.toggle();
				}

				entry = mapEntries.next();
			}
		}

	}-*/;

	public void removeImageLayer(String rowId) {
		removeImageLayer(this.aladinliteObj, rowId);
	}

	private native void removeImageLayer(JavaScriptObject aladinLiteJsObject, String rowId)/*-{
		aladinLiteJsObject.removeImageLayer(rowId);
	}-*/;


	/*****************************************************/
	/**** Catalog (~sources!) insert/remove methods ******/
	/*****************************************************/

	public JsArray<JavaScriptObject> createSourcesArray(List<TapServiceSourceObject> sources, String message) {
		JsArray<JavaScriptObject> sourcesArray = cds_getNativeJsArray();
		int index = 0;
		for (TapServiceSourceObject source : sources) {
			addSourceToArray(this.aladinliteObj, sourcesArray, source.getRa(), source.getDec(), message, index);
			index++;
		}
		return sourcesArray;
	}

	public JsArray<JavaScriptObject> createSourcesArrayWithNames(List<TapServiceSourceObject> sources) {
		JsArray<JavaScriptObject> sourcesArray = cds_getNativeJsArray();
		int index = 0;
		for (TapServiceSourceObject source : sources) {
			addSourceToArray(this.aladinliteObj, sourcesArray, source.getRa(), source.getDec(), source.getIauName(),
					index);
			index++;
		}
		return sourcesArray;
	}

	public JsArray<JavaScriptObject> createSourcesArrayWithNames(List<TapServiceSourceObject> sources,
			String catalogueName) {
		JsArray<JavaScriptObject> sourcesArray = cds_getNativeJsArray();
		int index = 0;
		for (TapServiceSourceObject source : sources) {
			addSourceToArrayWithDetails(this.aladinliteObj, sourcesArray, source.getRa(), source.getDec(),
					source.getIauName(), catalogueName, index);
			index++;
		}
		return sourcesArray;
	}

	private native void addSourceToArrayWithDetails(JavaScriptObject aladinLiteJsObject,
			JsArray<JavaScriptObject> sourcesArray, String ra, String dec, String sourceName, String catalogueName,
			int index) /*-{
		sourcesArray.push(aladinLiteJsObject.createSource(ra, dec, {
			sourcename : sourceName,
			catalogue : catalogueName,
			id : index
		}));
	}-*/;

	public JsArray<JavaScriptObject> createSingleSourceArray(String ra, String dec, String message) {
		JsArray<JavaScriptObject> sourcesArray = cds_getNativeJsArray();
		addSourceToArray(this.aladinliteObj, sourcesArray, ra, dec, message, 0);
		return sourcesArray;
	}

	private native void addSourceToArray(JavaScriptObject aladinLiteJsObject, JsArray<JavaScriptObject> sourcesArray,
			String ra, String dec, String message, int index) /*-{
		sourcesArray.push(aladinLiteJsObject.createSource(ra, dec, {
			message : message,
			id : index
		}));
	}-*/;

	public JavaScriptObject createCatalogAndAddSources(String catalogName, String catalogColor,
			JsArray<JavaScriptObject> sourcesArray) {
		return createCatalogAndAddSources(this.aladinliteObj, catalogName, catalogColor, sourcesArray);
	}

	private native JavaScriptObject createCatalogAndAddSources(JavaScriptObject aladinLiteJsObject, String catalogName,
			String catalogColor, JsArray<JavaScriptObject> sourcesArray) /*-{
		var newCatalog = $wnd.A.catalog({
			name : catalogName,
			color : catalogColor,
			sourceSize : 6
		});
		aladinLiteJsObject.addCatalog(newCatalog);
		newCatalog.addSources(sourcesArray);
		return newCatalog;
	}-*/;

	public JavaScriptObject createCatalog(String catalogName, String catalogColor, int sourceSizeValue) {
		return createCatalog(this.aladinliteObj, catalogName, catalogColor, sourceSizeValue);
	}

	private native JavaScriptObject createCatalog(JavaScriptObject aladinLiteJsObject, String catalogName,
			String catalogColor, int sourceSizeValue) /*-{
		var newCatalog = $wnd.A.catalog({
			name : catalogName,
			color : catalogColor,
			sourceSize : sourceSizeValue
		});
		aladinLiteJsObject.addCatalog(newCatalog);
		return newCatalog;
	}-*/;

	// public void addSourcesToCatalog(JavaScriptObject catalog,
	// JsArray<JavaScriptObject>
	// sourcesArray) {
	// addSourcesToCatalog(catalog, sourcesArray);
	// }

	public native void addSourcesToCatalog(JavaScriptObject catalog, JsArray<JavaScriptObject> sourcesArray) /*-{
		catalog.addSources(sourcesArray);
	}-*/;

	// public void selectSource(JavaScriptObject catalog, int id) {
	// selectSource(catalog, id);
	// }

	/**
	 * select the source at the given id within the catalog without removing the
	 * selection from previously selected sources
	 * 
	 * @param catalog
	 * @param id
	 */
	public native void selectSourceFromCatalogue(JavaScriptObject catalog, int id) /*-{
		if (catalog) {
			if (catalog != null) {
				var source = catalog.getSource(id);
				source.select();
			}
		}
	}-*/;

	/**
	 * remove the selection from all selected sources within a given catalog
	 * 
	 * @param catalog
	 */
	public native void cleanSelectionOnCatalogue(JavaScriptObject catalog) /*-{
		if (catalog) {
			if (catalog != null) {
				catalog.deselectAll();
			}
		}
	}-*/;

	/**
	 * deselect the source at the given id within the given catalog
	 * 
	 * @param catalog
	 * @param id
	 */
	public native void deselectSourceFromCatalogue(JavaScriptObject catalog, int id) /*-{
		if (catalog) {
			if (catalog != null) {
				var source = catalog.getSource(id);
				source.deselect();
			}
		}
	}-*/;

	public native void selectSource(JavaScriptObject catalog, int id) /*-{
		if (catalog) {
			if (catalog != null) {
				var source = catalog.getSource(id);
				if (source) {
					source.select();
				}
			}
		}
	}-*/;

	public void removeAllSourcesFromCatalog(JavaScriptObject catalog) {
		removeAllSourcesFromCatalog(this.aladinliteObj, catalog);
	}

	private native void removeAllSourcesFromCatalog(JavaScriptObject aladinLiteJsObject, JavaScriptObject catalog) /*-{
		if (catalog != null) {
			catalog.removeAll();
		}
		aladinLiteJsObject.view.requestRedraw();
	}-*/;

	public native void setCatalogSourceSize(JavaScriptObject catalog, int sourceSize) /*-{
		if (catalog) {
            if (catalog.primaryShape && catalog._shapeIsFunction) {
				var oldShape = catalog.shape;
				catalog.updateShape({shape: catalog.primaryShape, sourceSize: sourceSize});

				catalog.shape = oldShape;
				catalog._shapeIsFunction = true;
			} else {
				catalog.setSourceSize(sourceSize);
			}

		}
	}-*/;

	public native double getCatalogSourceSize(JavaScriptObject catalog) /*-{
		if (catalog) {
			 return catalog.getSourceSize();
		}
		return 1;
	}-*/;

	// Supported shapes: plus, cross, rhomb, triangle, circle ... else square will
	// be draw
	public native void setCatalogShape(JavaScriptObject catalog, String shape) /*-{
		// Draw new primary shape in cache and revert to shape function for secondary
		if (catalog) {
			var wasFunc = catalog._shapeIsFunction === true;
			var oldShape = catalog.shape;
			catalog.setShape(shape);

			if (wasFunc && catalog._shapeIsFunction !== true) {
				catalog.shape = oldShape;
				catalog._shapeIsFunction = true;
                catalog.primaryShape = shape;
			}
		}
	}-*/;



	public native void setCatalogArrowColor(JavaScriptObject catalog, String color) /*-{
		if (catalog) {
			 catalog.setArrowColor(color);
		}
	}-*/;

	public native void setCatalogArrowScale(JavaScriptObject catalog, double scale) /*-{
		if (catalog) {
			 catalog.setArrowScale(scale);
		}
	}-*/;

	public native void setCatalogAvgProperMotion(JavaScriptObject catalog, boolean removeAvgPM, boolean useMedian,
			boolean reportChange) /*-{
		if (catalog) {
			 catalog.setCatalogAvgProperMotion(removeAvgPM, useMedian,
						reportChange);
		}
	}-*/;

	/*****************************************************/
	/******* Footprints insert/remove methods ************/
	/*****************************************************/

	public JavaScriptObject createFootprintFromSTCS(String stcsFootprint) {
		stcsFootprint = correctFrame(stcsFootprint);
		return createFootprintsFromSTCS(this.aladinliteObj, stcsFootprint);
	}

	public JavaScriptObject createFootprintFromSTCS(String stcsFootprint, int shapeId) {
		stcsFootprint = correctFrame(stcsFootprint);
		return createFootprintsFromSTCS(this.aladinliteObj, stcsFootprint, shapeId);
	}

	public JavaScriptObject createTextLabel(String text, double[] raDecAngle) {
		return createTextLabel(this.aladinliteObj, text, raDecAngle);
	}

	private native JavaScriptObject createTextLabel(JavaScriptObject aladinLiteJsObject, String text,
			double[] raDecAngle) /*-{
		return aladinLiteJsObject.createTextLabel(text, raDecAngle[0],
				raDecAngle[1], raDecAngle[2]);
	}-*/;

	public JsArray<JavaScriptObject> createFootprintArrayFromSTCS(List<String> stcsFootprintList) {
		JsArray<JavaScriptObject> stcsFootprintArray = cds_getNativeJsArray();
		for (String stcsFootprint : stcsFootprintList) {
			stcsFootprint = correctFrame(stcsFootprint);
			stcsFootprintArray.push(createFootprintsFromSTCS(this.aladinliteObj, stcsFootprint));
		}
		return stcsFootprintArray;
	}

	public JsArray<JavaScriptObject> getNativeJsArray() {
		return cds_getNativeJsArray();
	}

	private native JsArray<JavaScriptObject> cds_getNativeJsArray() /*-{
		return [];
	}-*/;

	private native JavaScriptObject createFootprintsFromSTCS(JavaScriptObject aladinLiteJsObject,
			String inputStcsFootprint) /*-{
		return $wnd.A.footprintsFromSTCS(inputStcsFootprint);
	}-*/;

	private native JavaScriptObject createFootprintsFromSTCS(JavaScriptObject aladinLiteJsObject,
			String inputStcsFootprint, int shapeId) /*-{
		var polygons = $wnd.A.footprintsFromSTCS(inputStcsFootprint);
		for (var k = 0, len = polygons.length; k < len; k++) {
			polygons[k].id = shapeId;
		}
		return polygons;
	}-*/;

	private native String correctFrame(String stcs) /*-{
		stcs = stcs.replaceAll(/\'icrs\'/gi, "icrs");

		var matchCount = (stcs.match(/icrs|j2000|fk5/gi) || []).length;

        if (matchCount > 1) {
			var replacedString = stcs.replace(/icrs|j2000|fk5/gi, "icrs");
			return replacedString.replace(/(icrs\s*)+/gi, "icrs ");
		} else {
            return stcs;
		}
	}-*/;

	public void addFootprintToOverlay(JavaScriptObject overlay, JavaScriptObject footprint) {
		cds_addFootprintToOverlay(overlay, footprint);
	}

	private native void cds_addFootprintToOverlay(JavaScriptObject overlay, JavaScriptObject footprints)/*-{
    	if (Array.isArray(footprints) && footprints.length > 0) {
			var validFootprints = [];
			for (var i = 0; i < footprints.length; i++) {
				var item = footprints[i];
				if ((item.raDecArray && item.raDecArray.length > 0) || (item.centerRaDec && item.centerRaDec.length > 0)) {
                    item.setSelectionColor($wnd.increaseBrightness(overlay.color, 25));
                    item.setHoverColor($wnd.increaseBrightness(overlay.color, 25));
					validFootprints.push(item);
                }
			}
			overlay.addFootprints(validFootprints);
		} else {
            if ((footprints.raDecArray && footprints.raDecArray.length > 0) || (footprints.centerRaDec && footprints.centerRaDec.length > 0))
			footprints.setSelectionColor($wnd.increaseBrightness(overlay.color, 25));
			footprints.setHoverColor($wnd.increaseBrightness(overlay.color, 25));
			overlay.addFootprints([footprints]);
		}

	}-*/;

	public JavaScriptObject createOverlay(String overlayName, String overlayColor) {
		return createOverlay(overlayName, overlayColor, "solid");
	}

	public JavaScriptObject createOverlay(String overlayName, String overlayColor, String lineStyle) {
		return cds_createOverlay(this.aladinliteObj, overlayName, overlayColor, lineStyle);
	}

	private native JavaScriptObject cds_createOverlay(JavaScriptObject aladinLiteJavascriptObject, String overlayName,
			String overlayColor, String lineStyle) /*-{
		var aladinOverlay = aladinLiteJavascriptObject.createOverlay({
			name : overlayName,
			color : overlayColor,
			lineDash : $wnd.LineStyle().style2lineDash(lineStyle),
			noSmallCheck: true
		});
		aladinLiteJavascriptObject.addOverlay(aladinOverlay);
		return aladinOverlay;
	}-*/;

	public JavaScriptObject createOverlayAndAddFootprints(String overlayName, String overlayColor,
			JsArray<JavaScriptObject> stcsFootprintArray) {
		return createOverlayAndAddFootprints(overlayName, overlayColor, "solid", stcsFootprintArray);
	}

	public JavaScriptObject createOverlayAndAddFootprints(String overlayName, String overlayColor, String lineStyle,
			JsArray<JavaScriptObject> stcsFootprintArray) {
		return createOverlayAndAddFootprints(this.aladinliteObj, overlayName, overlayColor, lineStyle,
				stcsFootprintArray);
	}

	private native JavaScriptObject createOverlayAndAddFootprints(JavaScriptObject aladinLiteJsObject,
			String overlayName, String overlayColor, String lineStyle, JsArray<JavaScriptObject> stcsFootprintArray) /*-{
		var aladinOverlay = aladinLiteJsObject.createOverlay({
			name : overlayName,
			color : overlayColor,
			lineDash : $wnd.LineStyle().style2lineDash(lineStyle)
		});
		aladinLiteJsObject.addOverlay(aladinOverlay);
		for (var k = 0, len = stcsFootprintArray.length; k < len; k++) {
			aladinOverlay.addFootprints(stcsFootprintArray[k]);
		}
		return aladinOverlay;
	}-*/;

	public native void setOverlayColor(JavaScriptObject overlay, String color) /*-{
		if (overlay) {
            if (overlay.type === "catalog" && overlay.primaryShape && overlay._shapeIsFunction) {
                var catalog = overlay;
                var oldShape = catalog.shape;
                catalog.updateShape({shape: catalog.primaryShape, color: color});
                catalog.setSelectionColor($wnd.increaseBrightness(color, 25));
                catalog.setHoverColor($wnd.increaseBrightness(color, 25));

                catalog.shape = oldShape;
                catalog._shapeIsFunction = true;
            } else if (overlay.setSelectionColor && overlay.setHoverColor) {
                overlay.setSelectionColor($wnd.increaseBrightness(color, 25));
                overlay.setHoverColor($wnd.increaseBrightness(color, 25));
                overlay.setColor(color);
            } else {
                if (overlay.overlayItems) {
                    for (var i = 0; i < overlay.overlayItems.length; i++) {
                        var item = overlay.overlayItems[i];
                        item.setSelectionColor($wnd.increaseBrightness(color, 25));
                        item.setHoverColor($wnd.increaseBrightness(color, 25));
                        item.setColor(color);
                    }
                    overlay.setColor(color);
                }
            }
        }
	}-*/;

	public native void setOverlayLineWidth(JavaScriptObject overlay, int lineWidth) /*-{
		if (overlay) {
            overlay.setLineWidth(lineWidth);
            if (overlay.overlayItems) {
				for (var i=0; i < overlay.overlayItems.length; i++) {
					overlay.overlayItems[i].setLineWidth(lineWidth);
				}
			}
		}
	}-*/;

	public native void setOverlayLineStyle(JavaScriptObject overlay, String lineStyle) /*-{
		if (overlay) {
			 overlay.setLineDash($wnd.LineStyle().style2lineDash(lineStyle));
		}
	}-*/;

	public JavaScriptObject removeAllFootprintsFromOverlay(JavaScriptObject aladinOverlay) {
		return removeAllFootprintsFromOverlay(this.aladinliteObj, aladinOverlay);
	}

	private native JavaScriptObject removeAllFootprintsFromOverlay(JavaScriptObject aladinLiteJsObject,
			JavaScriptObject aladinOverlay) /*-{
		if (aladinOverlay) {
			aladinOverlay.removeAll();
			aladinLiteJsObject.view.requestRedraw();
		}
	}-*/;

	public native void selectShape(JavaScriptObject jsShape) /*-{
		if (jsShape) {
			if (Array.isArray(jsShape)) {
				for (var k = 0, len = jsShape.length; k < len; k++) {
					jsShape[k].select();
				}
			} else {
				jsShape.select();
			}
		}
	}-*/;

	public native void deselectShape(JavaScriptObject jsShape) /*-{
		if (jsShape) {
			if (Array.isArray(jsShape)) {
				for (var k = 0, len = jsShape.length; k < len; k++) {
					jsShape[k].deselect();
				}
			} else {
				jsShape.deselect();
			}
		}
	}-*/;

	public native void hoverStart(JavaScriptObject jsShape) /*-{
		if (jsShape) {
			if (Array.isArray(jsShape)) {
				for (var k = 0, len = jsShape.length; k < len; k++) {
					jsShape[k].hover();
				}
			} else {
				jsShape.hover();
			}
		}
	}-*/;

	public native void hoverStop(JavaScriptObject jsShape) /*-{
		if (jsShape) {
			if (Array.isArray(jsShape)) {
				for (var k = 0, len = jsShape.length; k < len; k++) {
					jsShape[k].unhover();
				}
			} else {
				jsShape.unhover();
			}
		}
	}-*/;

	public native void showShape(JavaScriptObject jsShape) /*-{
		if (jsShape) {
			if (Array.isArray(jsShape)) {
				for (var k = 0, len = jsShape.length; k < len; k++) {
					jsShape[k].show();
				}
			} else {
				jsShape.show();
			}
		}
	}-*/;

	public native void hideShape(JavaScriptObject jsShape) /*-{
		if (jsShape) {
			if (Array.isArray(jsShape)) {
				for (var k = 0, len = jsShape.length; k < len; k++) {
					jsShape[k].hide();
				}
			} else {
				jsShape.hide();
			}
		}
	}-*/;

	/*****************************************************/
	/*************** Color Map Actions *******************/
	/*****************************************************/

	/**
	 * setDefaultColorPalette().
	 * 
	 * @param colorPalette Input Enum ColorPalette
	 */
	@Override
	public final void setColorPalette(String layerId, final ColorPalette colorPalette) {

		switch (colorPalette) {
		case GREYSCALE:
			setGrayscaleColorMap(layerId);
			break;
		case EOSB:
			setEosbColorMap(layerId);
			break;
		case RAINBOW:
			setRainbowColorMap(layerId);
			break;
		case PLANCK:
			setPlanckColorMap(layerId);
			break;
		case CUBEHELIX:
			setCubehelixColorMap(layerId);
			break;
		default:
			setNativeColorMap(layerId);
			break;

		}
	}

	@Override
	public void reverseColorMap() {
		reverseColorMap(this.aladinliteObj);
	}

	private native void reverseColorMap(JavaScriptObject aladinLiteJsObject) /*-{
		var layer = aladinLiteJsObject.getBaseImageLayer();
		layer.setColormap(layer.colorCfg.colormap, {reversed: true});
	}-*/;

	@Override
	public void setRainbowColorMap(String layerId) {
		setRainbowColorMap(this.aladinliteObj, layerId);
	}

	private native void setRainbowColorMap(JavaScriptObject aladinLiteJsObject, String layerId) /*-{
		var layer = aladinLiteJsObject.getOverlayImageLayer(layerId);
		layer.setColormap('rainbow');
	}-*/;

	@Override
	public void setEosbColorMap(String layerId) {
		setEosbColorMap(this.aladinliteObj, layerId);
	}

	private native void setEosbColorMap(JavaScriptObject aladinLiteJsObject, String layerId) /*-{
		var layer = aladinLiteJsObject.getOverlayImageLayer(layerId);
		layer.setColormap('eosb');
	}-*/;

	@Override
	public void setNativeColorMap(String layerId) {
		setNativeColorMap(this.aladinliteObj, layerId);
	}

	private native void setNativeColorMap(JavaScriptObject aladinLiteJsObject, String layerId) /*-{
		var layer = aladinLiteJsObject.getOverlayImageLayer(layerId);
		layer.setColormap('native');
	}-*/;

	@Override
	public void setGrayscaleColorMap(String layerId) {
		setGrayscaleColorMap(this.aladinliteObj, layerId);
	}

	private native void setGrayscaleColorMap(JavaScriptObject aladinLiteJsObject, String layerId) /*-{
		var layer = aladinLiteJsObject.getOverlayImageLayer(layerId);
    	layer.setColormap('grayscale');
	}-*/;

	@Override
	public void setPlanckColorMap(String layerId) {
		setPlanckColorMap(this.aladinliteObj, layerId);
	}

	private native void setPlanckColorMap(JavaScriptObject aladinLiteJsObject, String layerId) /*-{
		var layer = aladinLiteJsObject.getOverlayImageLayer(layerId);
		layer.setColormap('planck');
	}-*/;

	@Override
	public void setCMBColorMap(String layerId) {
		setCMBColorMap(this.aladinliteObj, layerId);
	}

	private native void setCMBColorMap(JavaScriptObject aladinLiteJsObject, String layerId) /*-{
		var layer = aladinLiteJsObject.getOverlayImageLayer(layerId);
		layer.setColormap('cmb');
	}-*/;

	@Override
	public void setCubehelixColorMap(String layerId) {
		setCubehelixColorMap(this.aladinliteObj, layerId);
	}

	private native void setCubehelixColorMap(JavaScriptObject aladinLiteJsObject, String layerId) /*-{
		var layer = aladinLiteJsObject.getOverlayImageLayer(layerId);
		layer.setColormap('cubehelix');
	}-*/;


	/*****************************************************/
	/******* Get latitude/longitude Actions **************/
	/*****************************************************/
	public CoordinatesObject getRaDec(int x, int y) {
		return getRaDec(this.aladinliteObj, x, y);
	}

	private native CoordinatesObject getRaDec(JavaScriptObject aladinLiteJsObject, int x, int y) /*-{
		return aladinLiteJsObject.getRaDec(x, y);
	}-*/;

	public double getCenterLongitudeDeg() {
		return getCenterLongitudeDeg(this.aladinliteObj);
	}

	private native double getCenterLongitudeDeg(JavaScriptObject aladinLiteJsObject) /*-{
		return aladinLiteJsObject.view.viewCenter.lon;
	}-*/;

	public double getCenterLatitudeDeg() {
		return getCenterLatitudeDeg(this.aladinliteObj);
	}

	private native double getCenterLatitudeDeg(JavaScriptObject aladinLiteJsObject) /*-{
		return aladinLiteJsObject.view.viewCenter.lat;
	}-*/;

	public double getFovDeg() {
		return getFovDeg(this.aladinliteObj);
	}

	private native double getFovDeg(JavaScriptObject aladinLiteJsObject) /*-{
		return aladinLiteJsObject.view.fov;
	}-*/;

	public JavaScriptObject getFovCorners(int points) {
		return getFovCorners(this.aladinliteObj, points);
	}

	private native JavaScriptObject getFovCorners(JavaScriptObject aladinLiteJsObject, int points) /*-{
		return aladinLiteJsObject.getFoVCorners(points, "icrs");
	}-*/;

	public void showReticle(boolean show) {
		showReticle(this.aladinliteObj, show);
	}

	private native void showReticle(JavaScriptObject aladinLiteJsObject, boolean show) /*-{
		aladinLiteJsObject.showReticle(show);
	}-*/;

	public void fixLayoutDimensions() {
		fixLayoutDimensions(this.aladinliteObj);
	}

	private native void fixLayoutDimensions(JavaScriptObject aladinLiteJsObject) /*-{
		aladinLiteJsObject.view.fixLayoutDimensions();
	}-*/;

	@Override
	public void setZoom(double zoomDegrees) {
		setZoom(this.aladinliteObj, zoomDegrees);
	}

	private native void setZoom(JavaScriptObject aladinLiteJsObject, double zoomDegrees) /*-{
		aladinLiteJsObject.view.setZoom(zoomDegrees);
	}-*/;

	public void resize(double newWidth, double newHeight, Unit newUnit) {
		this.aladinLiteGwtDiv.getElement().getStyle().setWidth(newWidth, newUnit);
		this.aladinLiteGwtDiv.getElement().getStyle().setHeight(newHeight, newUnit);
		fixLayoutDimensions();
	}

	public void requestRedraw() {
		requestRedraw(this.aladinliteObj);
	}

	private native void requestRedraw(JavaScriptObject aladinLiteJsObject) /*-{
		aladinLiteJsObject.view.requestRedraw();
	}-*/;

	/**
	 * NEW API. From here on new AladinLite API using CDS facade as per Thomas Bock
	 * suggestion
	 *
	 */

	/**
	 * It adds a new source to hte given catalogue
	 * 
	 * @param jsCatalogue JS object representing the catalogue where to add the
	 *                    jsSource
	 * @param jsSource    JS obj representing the source to be added to the
	 *                    catalogue
	 */
	public void newApi_addSourceToCatalogue(JavaScriptObject jsCatalogue, JavaScriptObject jsSource) {
		JsArray<JavaScriptObject> sourcesArray = cds_getNativeJsArray();
		sourcesArray.push(jsSource);
		addSourcesToCatalog(jsCatalogue, sourcesArray);
	}

	/**
	 * It creates a JS obj source with the passed details as extra info
	 * 
	 * @param ra      source right ascension
	 * @param dec     source declination
	 * @param details extra parameters (e.g. message, id, ...)
	 * @return a JS obj representing the source
	 */
	public <T> JavaScriptObject newApi_createSourceJSObj(String ra, String dec, Map<String, T> details) {
		JSONObject jsonDetails = createSourceDetails(details);

		return newCDSApi_createSource(ra, dec, jsonDetails.getJavaScriptObject());
	}

	public <T> JavaScriptObject newApi_createSourceJSObj(String ra, String dec, Map<String, T> details, int shapeId) {
		JSONObject jsonDetails = createSourceDetails(details);

		return newCDSApi_createSource(ra, dec, jsonDetails.getJavaScriptObject(), shapeId);
	}

	private <T> JSONObject createSourceDetails(Map<String, T> details) {
		JSONObject jsonDetails = new JSONObject();
		if (null != details) {
			for (Entry<String, T> currDetail : details.entrySet()) {
				if (null != currDetail.getValue()) {
					if (currDetail.getValue() instanceof String) {
						// Log.debug(currDetail.getKey() + " " + currDetail.getValue());
						jsonDetails.put(currDetail.getKey(), new JSONString((String) currDetail.getValue()));
					} else if (currDetail.getValue() instanceof Integer) {
						jsonDetails.put(currDetail.getKey(),
								new JSONNumber(((Integer) currDetail.getValue()).doubleValue()));
					} else if (currDetail.getValue() instanceof Float) {
						jsonDetails.put(currDetail.getKey(),
								new JSONNumber(((Float) currDetail.getValue()).doubleValue()));
					} else if (currDetail.getValue() instanceof Double) {
						jsonDetails.put(currDetail.getKey(), new JSONNumber((Double) currDetail.getValue()));
					}
				}

			}
		}
		return jsonDetails;
	}

	/**
	 * It creates a JS obj source with the passed details as extra info
	 * 
	 * @param ra      source right ascension
	 * @param dec     source declination
	 * @param details extra parameters (e.g. message, id, ...)
	 * @return
	 */
	private native JavaScriptObject newCDSApi_createSource(String ra, String dec, JavaScriptObject details) /*-{
		return new $wnd.A.source(ra, dec, details, null);

	}-*/;

	private native JavaScriptObject newCDSApi_createSource(String ra, String dec, JavaScriptObject details, int shapeId) /*-{
		var source = new $wnd.A.source(ra, dec, details, null);
		source.id = shapeId;
		return source;
	}-*/;

	public JavaScriptObject createImageMarker(String imgSourceURL) {
		return cds_createImageMarker(imgSourceURL);
	}

	private native JavaScriptObject cds_createImageMarker(String imgSourceURL)/*-{
		var customImg = new $wnd.Image();
		customImg.src = imgSourceURL;
		return customImg;
	}-*/;

	public JavaScriptObject createFunctionPointer(String functionName) {
		return cds_createFunctionPointer(functionName);
	}

	private native JavaScriptObject cds_createFunctionPointer(String functionName)/*-{
		return $wnd[functionName];
	}-*/;

	public <T> JavaScriptObject createCatalogWithDetails(String catalogName, int sourceSize, String color,
			Map<String, T> details) {

		JSONObject jsonDetails = new JSONObject();

		jsonDetails.put("name", new JSONString(catalogName));
		jsonDetails.put("sourceSize", new JSONNumber(sourceSize));
		jsonDetails.put("color", new JSONString(color));
		jsonDetails.put("hoverColor", new JSONString(AladinLiteUtils.increaseBrightness(color, 25)));
		if (null != details) {
			for (Entry<String, T> currDetail : details.entrySet()) {
				if (null != currDetail.getValue()) {
					if (currDetail.getValue() instanceof String) {
						// Log.debug(currDetail.getKey() + " " + currDetail.getValue());
						jsonDetails.put(currDetail.getKey(), new JSONString((String) currDetail.getValue()));
					} else if (currDetail.getValue() instanceof Integer) {
						jsonDetails.put(currDetail.getKey(),
								new JSONNumber(((Integer) currDetail.getValue()).doubleValue()));
					} else if (currDetail.getValue() instanceof Float) {
						jsonDetails.put(currDetail.getKey(),
								new JSONNumber(((Float) currDetail.getValue()).doubleValue()));
					} else if (currDetail.getValue() instanceof Double) {
						jsonDetails.put(currDetail.getKey(), new JSONNumber((Double) currDetail.getValue()));
					} else if (currDetail.getValue() instanceof JavaScriptObject) {
						jsonDetails.put(currDetail.getKey(), new JSONObject((JavaScriptObject) currDetail.getValue()));
					}

				}

			}
		}
		return cds_createCatalog(jsonDetails.getJavaScriptObject());
	}

	private native JavaScriptObject cds_createCatalog(JavaScriptObject details)/*-{
		return $wnd.A.catalog(details);
	}-*/;

	public String getViewURL() {
		return cds_getViewDataURL(this.aladinliteObj, false);
	}

	public String getViewURL(boolean includeOpenSeaDragon) {
		return cds_getViewDataURL(this.aladinliteObj, includeOpenSeaDragon);
	}

	private native String cds_getViewDataURL(JavaScriptObject aladinObj, boolean includeOpenSeaDragon)/*-{
		return aladinObj.view.getCanvasDataURLCopy({
			includeOpenSeaDragon : includeOpenSeaDragon
		});
	}-*/;

	public JavaScriptObject getViewCanvas() {
		return getViewCanvas(false);
	}

	public JavaScriptObject getViewCanvas(boolean includeOpenSeaDragon) {
		return cds_getViewCanvas(this.aladinliteObj, includeOpenSeaDragon);
	}

	private native JavaScriptObject cds_getViewCanvas(JavaScriptObject aladinObj, boolean includeOpenSeaDragon)/*-{
		return includeOpenSeaDragon === true
				&& aladinObj.view.getCanvasWithOpenSeaDragon()
				|| aladinObj.view.getCanvasCopy();
	}-*/;

	// public void increaseZoomOnTouch(double step) {
	// cds_increaseZoomOnTouch(this.aladinLiteJavascriptObject, step);
	// }
	//
	// private native void cds_increaseZoomOnTouch(JavaScriptObject aladinObj,
	// double step)/*-{
	// aladinObj.view.setZoomLevel(aladinObj.view.zoomLevel + step);
	// }-*/;
	//
	// public void decreaseZoomOnTouch(double step) {
	// cds_decreaseZoomOnTouch(this.aladinLiteJavascriptObject, step);
	// }
	//
	// private native void cds_decreaseZoomOnTouch(JavaScriptObject aladinObj,
	// double step)/*-{
	// aladinObj.view.setZoomLevel(aladinObj.view.zoomLevel + step);
	// }-*/;

	public void showGrid(boolean showGrid) {
		cds_showGrid(this.aladinliteObj, showGrid);
	}

	private native void cds_showGrid(JavaScriptObject aladinObj, boolean showGrid)/*-{
    	if (showGrid) {
			aladinObj.showCooGrid();
		} else {
            aladinObj.hideCooGrid();
		}
	}-*/;

	public JavaScriptObject createMOC(String optionsString) {
		JavaScriptObject optionsJSON = JsonUtils.safeEval(optionsString);

		return cds_createMOC(this.aladinliteObj, optionsJSON);
	}

	private native JavaScriptObject cds_createMOC(JavaScriptObject aladinObj, JavaScriptObject optionsJSON)/*-{
		return aladinObj.createMOC(optionsJSON);
	}-*/;

	public JavaScriptObject createQ3CMOC(String optionsString) {
		JavaScriptObject optionsJSON = JsonUtils.safeEval(optionsString);

		return cds_createQ3CMOC(this.aladinliteObj, optionsJSON);
	}

	public JavaScriptObject createQ3CMOC(JavaScriptObject options) {

		return cds_createQ3CMOC(this.aladinliteObj, options);
	}

	private native JavaScriptObject cds_createQ3CMOC(JavaScriptObject aladinObj, JavaScriptObject optionsJSON)/*-{
		return aladinObj.createQ3CMOC(optionsJSON);
	}-*/;

	public JavaScriptObject createHealpixMoc(JavaScriptObject data, JavaScriptObject options) {
		return createHealpixMoc(this.aladinliteObj, data, options);
	}

	private native JavaScriptObject createHealpixMoc(JavaScriptObject aladinObj, JavaScriptObject data, JavaScriptObject optionsJSON)/*-{
		return $wnd.A.MOCFromJSON(data, optionsJSON);
	}-*/;


	public void setMOCOpacity(JavaScriptObject moc, double opacity) {
		cds_setMOCOpacity(this.aladinliteObj, moc, opacity);
	}

	private native void cds_setMOCOpacity(JavaScriptObject aladinObj, JavaScriptObject moc, double opacity)/*-{
		moc.setOpacity(opacity);
	}-*/;

	public void clearMOC(JavaScriptObject moc) {
		cds_clearMOC(this.aladinliteObj, moc);
	}

	private native void cds_clearMOC(JavaScriptObject aladinObj, JavaScriptObject moc)/*-{
		moc.clearAll();
	}-*/;

	public void addMOCData(JavaScriptObject moc, String mocData) {
		JavaScriptObject mocJSON = JsonUtils.safeEval(mocData);
		cds_addMOCData(moc, mocJSON);
	}

	private native void cds_addMOCData(JavaScriptObject moc, JavaScriptObject mocJSON)/*-{
		moc.dataFromJSON(mocJSON);
	}-*/;

	public void addMOC(JavaScriptObject moc) {
		cds_addMOC(this.aladinliteObj, moc);
	}

	private native void cds_addMOC(JavaScriptObject aladinObj, JavaScriptObject moc)/*-{
		aladinObj.addMOC(moc);
	}-*/;

	public void removeMOC(JavaScriptObject moc) {
		cds_removeMOC(this.aladinliteObj, moc);
	}

	private native void cds_removeMOC(JavaScriptObject aladinObj, JavaScriptObject moc)/*-{
		aladinObj.removeLayer(moc.getOverlay())
		aladinObj.removeLayer(moc);
	}-*/;

	public JavaScriptObject getHealpixBorder(int order, int ipix) {
		return (cds_getHealpixBorder(this.aladinliteObj, order, ipix));
	}

	private native JavaScriptObject cds_getHealpixBorder(JavaScriptObject aladinObj, int order, int ipix)/*-{
		return aladinObj.getHealpixBorder(order, ipix);
	}-*/;

	public JavaScriptObject getVisibleNpix(int order) {
		return cds_getVisibleNpix(this.aladinliteObj, order);
	};

	private native JavaScriptObject cds_getVisibleNpix(JavaScriptObject aladinObj, int order)/*-{
		return aladinObj.getVisibleNpix(order);
	}-*/;

	public void triggerMouseWheelEvent(Event event) {
		cds_triggerMouseWheelEvent(this.aladinliteObj, event);
	}

	private native void cds_triggerMouseWheelEvent(JavaScriptObject aladinObj, Event event)/*-{
		var newEvent = new Event("wheel");
		newEvent.detail = event.detail;
		newEvent.deltaY = event.detail;
		aladinObj.view.reticleCanvas.dispatchEvent(newEvent)
	}-*/;


	public JavaScriptObject getVisiblePixelsInMOC(JavaScriptObject moc) {
		return getVisiblePixelsInMOC(this.aladinliteObj, moc);
	}

	private native JavaScriptObject getVisiblePixelsInMOC(JavaScriptObject aladinObj, JavaScriptObject moc)/*-{
		return aladinObj.getVisiblePixelsInMOC(moc);
	}-*/;

	public int getVisibleCountInMOC(JavaScriptObject moc) {
		return cds_getVisibleCountInMOC(this.aladinliteObj, moc);
	};

	private native int cds_getVisibleCountInMOC(JavaScriptObject aladinObj, JavaScriptObject moc)/*-{
		return aladinObj.getVisibleCountInMOC(moc);
	}-*/;

	public JavaScriptObject getVisiblePixelsInMOC(JavaScriptObject moc, int targetOrder, boolean hasToBeLeaf) {
		return cds_getVisiblePixelsInMOC(this.aladinliteObj, moc, targetOrder, hasToBeLeaf);
	};

	private native JavaScriptObject cds_getVisiblePixelsInMOC(JavaScriptObject aladinObj, JavaScriptObject moc,
			int targetOrder, boolean hasToBeLeaf)/*-{
		return aladinObj.getVisiblePixelsTargetOrderInMOC(moc, targetOrder,
				hasToBeLeaf);
	}-*/;


	public void setSelectionMode(String mode) {
		setSelectionMode(this.aladinliteObj, this, mode);
	};

	private native void setSelectionMode(JavaScriptObject aladinObj, AladinLiteWidget instance, String mode)/*-{
		if (mode === "box") {
			mode = "rect";
		} else if (mode === "polygon") {
			mode = "poly";
		}
		try {
			aladinObj.select(mode, function (area, objects) {

                var deselect = false;
                if (this.coo && this.startCoo) {
                    deselect = this.coo.x < this.startCoo.x;
				}

				var isShapeSelect = aladinObj.view.isShapeSelect();
				// Flatten catalogue arrays
				objects = [].concat.apply([], objects);

				if (area.label === "circle") {
					var lonLat = aladinObj.view.wasm.pix2world(area.x, area.y);
					var raDec = aladinObj.view.wasm.viewToICRSCooSys(lonLat[0], lonLat[1]);
					var radius = area.r;

					if (aladinObj.view.fov < 180) {
						radius = area.r / (aladinObj.view.width) * aladinObj.view.fov;
					} else {
						var lonlat1 = aladinObj.view.wasm.pix2world(aladinObj.view.width / 2, aladinObj.view.height / 2);
						var lonlat2 = aladinObj.view.wasm.pix2world(aladinObj.view.width / 2 + 100, aladinObj.view.height / 2);

						var dist = $wnd.A.coo(lonlat1[0], lonlat1[1], 5).distance($wnd.A.coo(lonlat2[0], lonlat2[1], 7));
						radius = area.r / 100 * dist;
					}

					if (isShapeSelect) {
						var rarea = {type: "CIRCLE", circle: {ra: raDec[0], dec: raDec[1], radius: radius}};

                        if (deselect) {
							instance.@esac.archive.absi.modules.cl.aladinlite.widget.client.AladinLiteWidget::fireDeselectAreaEvent(*)(objects, rarea);
						} else {
							instance.@esac.archive.absi.modules.cl.aladinlite.widget.client.AladinLiteWidget::fireSelectAreaEvent(*)(objects, rarea);
						}

					} else {
						aladinObj.view.searchArea['points'] = [{ra: raDec[0], dec: raDec[1]}];
						aladinObj.view.searchArea['radius'] = radius;
						aladinObj.view.searchArea['type'] = 'circle';
						aladinObj.view.filterOverlay.overlayItems = [];
						aladinObj.view.filterOverlay.add($wnd.A.circle(raDec[0], raDec[1], radius, {}), false);
					}

				} else if (area.label === "rect") {
					var points = [];
					point1 = aladinObj.view.wasm.pix2world(area.x, area.y);
					point2 = aladinObj.view.wasm.pix2world(area.x + area.w, area.y);
					point3 = aladinObj.view.wasm.pix2world(area.x + area.w, area.y + area.h);
					point4 = aladinObj.view.wasm.pix2world(area.x, area.y + area.h);

					points[0] = {ra: point1[0], dec: point1[1]};
					points[1] = {ra: point2[0], dec: point2[1]};
					points[2] = {ra: point3[0], dec: point3[1]};
					points[3] = {ra: point4[0], dec: point4[1]};

					if (isShapeSelect) {
						var rarea = {points: points, type: "BOX"}
						if (deselect) {
							instance.@esac.archive.absi.modules.cl.aladinlite.widget.client.AladinLiteWidget::fireDeselectAreaEvent(*)(objects, rarea);
						} else {
							instance.@esac.archive.absi.modules.cl.aladinlite.widget.client.AladinLiteWidget::fireSelectAreaEvent(*)(objects, rarea);
						}
					} else {
						aladinObj.view.searchArea['points'] = points;
						aladinObj.view.searchArea['type'] = 'BOX';
						aladinObj.view.filterOverlay.overlayItems = [];
						aladinObj.view.filterOverlay.add($wnd.A.polygon(points.map(function (point) {
							return aladinObj.view.wasm.viewToICRSCooSys(point.ra, point.dec);
						}), {fill: true, fillColor: 'rgba(255, 255, 255, 0)', closed: true}), false);
					}

				} else {
					var points = [];

					for (var i = 0; i < area.vertices.length; i++) {
						var vertex = area.vertices[i];
						var point = aladinObj.view.wasm.pix2world(vertex.x, vertex.y);
						points[i] = {ra: point[0], dec: point[1]};
					}

					if (isShapeSelect) {
						var rarea = {points: points, type: "POLYGON"}
						instance.@esac.archive.absi.modules.cl.aladinlite.widget.client.AladinLiteWidget::fireSelectAreaEvent(*)(objects, rarea);
					} else {
						aladinObj.view.searchArea['points'] = points;
						aladinObj.view.searchArea['type'] = 'POLYGON';
						aladinObj.view.filterOverlay.overlayItems = [];
						aladinObj.view.filterOverlay.add($wnd.A.polygon(points.map(function (point) {
							return aladinObj.view.wasm.viewToICRSCooSys(point.ra, point.dec);
						}), {fill: true, fillColor: 'rgba(255, 255, 255, 0)', closed: true}), false);
					}

				}


				if (!isShapeSelect) {
					var selectSearchAreaFunction = aladinObj.view.aladin.callbacksByEventName['selectSearchArea'];
					if (typeof selectSearchAreaFunction === 'function') {
						selectSearchAreaFunction(aladinObj.view.searchArea);
					}
				}


			});
		} catch (_) {

		}

	}-*/;

	public String getSelectionMode() {
		return getSelectionMode(this.aladinliteObj);
	};

	private native String getSelectionMode(JavaScriptObject aladinObj)/*-{
		return aladinObj.getSelectMode();
	}-*/;

	public void setSelectionType(String type) {
		setSelectionType(this.aladinliteObj, type);
	}

	private native void setSelectionType(JavaScriptObject aladinObj, String type)/*-{
		aladinObj.view.setSelectionType(type);
	}-*/;

	public void startSelectionMode() {
		startSelectionMode(this.aladinliteObj);
	};

	private native void startSelectionMode(JavaScriptObject aladinObj)/*-{
//		aladinObj.fire("selectstart");
	}-*/;

	public void endSelectionMode() {
		endSelectionMode(this.aladinliteObj);
	};

	private native void endSelectionMode(JavaScriptObject aladinObj)/*-{
    	if (aladinObj.view.selector) {
			aladinObj.view.selector.select = null;
			aladinObj.view.aladin.showReticle(true)
			aladinObj.view.setCursor('default');
			aladinObj.view.setMode(0);
			aladinObj.view.requestRedraw();
		}
	}-*/;

	public void clearSearchArea() {
		clearSearchArea(this.aladinliteObj);
	}

	private native void clearSearchArea(JavaScriptObject aladinObj)/*-{
		aladinObj.view.clearSearchArea();
	}-*/;

	public void createSearchArea(String searchAreaStcs) {
		createSearchArea(this.aladinliteObj, searchAreaStcs);
	}

	private native void createSearchArea(JavaScriptObject aladinObj, String searchAreaStcs)/*-{
		var getDecFromCoords = function(input) {
			var decimalTypeRegex = /^[+-]?(\d*\.)?\d+$/;
			if (decimalTypeRegex.test(input)) { // Decimal  degrees
				return parseFloat(input);
			} else if (input.endsWith("r") || input.endsWith("rad")) { // Radian
				var radianValueRegex = /^[+-]?(\d*\.)?\d+/;
				var radianValue = input.match(radianValueRegex)[0];
				return parseFloat(radianValue) * 180 / Math.PI;
			} else if (input.includes("m") && input.includes("s")) { // HMS / DMS
				if (input.includes("h")) {
					var hmsValueArr = input.split(/h|m|s/);
					var hours = Math.floor(parseFloat(hmsValueArr[0]));
					var minutes = Math.floor(parseFloat(hmsValueArr[1]));
					var seconds = Math.floor(parseFloat(hmsValueArr[2]));

					if (hours < 0 || hours > 24) {
						throw new Error("HMS: Hour value must be between 0 - 24");
					} else if (minutes < 0 || minutes > 60) {
						throw new Error("HMS: Minute value must be between 0 - 60");
					} else if (seconds < 0 || seconds > 60) {
						throw new Error("HMS: Second value must be between 0 - 60");
					}

					return 15 * parseFloat(hmsValueArr[0])
							+ 15 * parseFloat(hmsValueArr[1]) / 60
							+ 15 * parseFloat(hmsValueArr[2]) / 3600;

				} else if (input.includes("d")) {
					var dmsValueArr = input.split(/d|m|s/);
					var degrees = Math.floor(parseFloat(dmsValueArr[0]));
					var minutes = Math.floor(parseFloat(dmsValueArr[1]));
					var seconds = parseFloat(dmsValueArr[2]);

					if (degrees > 90 || degrees < -90) {
						throw new Error("DMS: Degree value must be between -90 - 90");
					} else if (minutes > 60 || minutes < 0) {
						throw new Error("DMS: Minute value must be between 0 - 60");
					} else if (seconds < 0 || seconds > 60) {
						throw new Error("DMS: Second value must be between 0 - 60");
					}

					return parseFloat(degrees)
							+ (parseFloat(minutes) / 60)
							+ (parseFloat(seconds) / 3600);

				}
			}
			throw new Error("Coordinate format was not recognized.");
		};

		var parseSTCS = function(stcs) {

			var shapeStr = stcs.toLowerCase().trim();
			var validType = /^(polygon|circle|box)/;
			if (validType.test(shapeStr)) {
				var type = shapeStr.match(validType)[0];
				var valueStr = shapeStr.replace(type, "").replace("icrs", "").trim();
				var valueArr = valueStr.split(' ');

				if (type === 'circle') {
					if (valueArr.length === 3) {
						var ra = parseFloat(valueArr[0]);
						var dec = parseFloat(valueArr[1]);
						var radius = parseFloat(valueArr[2]);
						return {type: "CIRCLE", points: [{ra: ra, dec: dec}], radius: radius};
					} else {
						throw new Error("Circle takes three arguments but " + valueArr.length + " were provided");
					}
				} else {
					if (type === 'box' && valueArr.length !== 8) {
						throw new Error("Box takes 8 arguments but " + valueArr.length + " were provided");
					} else if (valueArr.length % 2 !== 0) {
						throw new Error("There must be an even number of RA and DEC values");
					}


					var points = [];
					for (var i = 0; i < valueArr.length; i += 2) {
						var ra = getDecFromCoords(valueArr[i]);
						var dec = getDecFromCoords(valueArr[i + 1]);
						points.push({ra: ra, dec: dec});
					}

					return {type: type.toUpperCase(), points: points};
				}


			} else {
				throw new Error("Shape type not recognized.")
			}
		};

    	var searchArea = parseSTCS(searchAreaStcs);

		aladinObj.view.filterOverlay.removeAll();

		aladinObj.view.searchArea = searchArea;

		if (searchArea['type'] === "CIRCLE") {
			var point = searchArea['points'][0];
			var coords = [point.ra, point.dec];
			aladinObj.view.searchArea['points_j2000'] = [{ra: point.ra, dec: point.dec}];
			aladinObj.view.filterOverlay.add($wnd.A.circle(coords[0], coords[1], searchArea['radius'], {}), false)
		} else {
			var coords = aladinObj.view.searchArea['points'].map(function (point) {
				return [point.ra, point.dec];
			});
			aladinObj.view.searchArea['points_j2000'] = coords.map(function (point) {
				return [{ra: point[0], dec: point[1]}]
			}).flat();

			aladinObj.view.filterOverlay.add($wnd.A.polygon(coords, {fill: true, fillColor: 'rgba(255, 255, 255, 0)', closed: true}), false);
		}

		var selectSearchAreaFunction = aladinObj.view.aladin.callbacksByEventName['selectSearchArea'];
		if(typeof selectSearchAreaFunction === 'function'){
			selectSearchAreaFunction(aladinObj.view.searchArea);
		}

		aladinObj.view.requestRedraw();
	}-*/;

	public void addOpenSeaDragon(JavaScriptObject openSeaDragonObject) {
		addOpenSeaDragon(this.aladinliteObj, openSeaDragonObject);
	};

	private native void addOpenSeaDragon(JavaScriptObject aladinObj, JavaScriptObject openSeaDragonObject)/*-{
		aladinObj.addOpenSeaDragon(openSeaDragonObject);
	}-*/;

	public void setOpenSeaDragonOpacity(double opacity) {
		setOpenSeaDragonOpacity(this.aladinliteObj, opacity);
	};

	private native void setOpenSeaDragonOpacity(JavaScriptObject aladinObj, double opacity)/*-{
		aladinObj.setOpenSeaDragonOpacity(opacity);
	}-*/;

	public void removeOpenSeaDragon(String name) {
		removeOpenSeaDragon(this.aladinliteObj, name);
	};

	private native void removeOpenSeaDragon(JavaScriptObject aladinObj, String name)/*-{
		aladinObj.removeOpenSeaDragon(name);
	}-*/;


	public double[] getFov() {
		return getFov(this.aladinliteObj);
	}

	private native double[] getFov(JavaScriptObject aladinObj) /*-{
		return aladinObj.getFov();
	}-*/;


	public void handleFilesFromJS(String fileName, double fileSize, JavaScriptObject callback) {
		if (fileSize > 1 * 1024 * 1024 * 1024) { // Check if file size exceeds 1GB
			fireConfirmDialogRequested("aladin_file_size_warn_title", "aladin_file_size_warn_body", "aladin_file_size_warn_help", callback);
		} else {
			callJsCallback(callback, true);
		}
	}

	private native void callJsCallback(JavaScriptObject callback, boolean success) /*-{
		callback(success);
	}-*/;


	private native void exportFileDropHandler(AladinLiteWidget instance) /*-{
		$wnd.handleFilesFromJS = $entry(function(files, callback) {
			var largestFile = files[0];
			for (var i = 1; i < files.length; i++) {
				if (files[i].size > largestFile.size) {
					largestFile = files[i];
				}
			}
			var largestFileName = largestFile.name;
			var largestFileSize = largestFile.size;
			instance.@esac.archive.absi.modules.cl.aladinlite.widget.client.AladinLiteWidget::handleFilesFromJS(*)(largestFileName, largestFileSize, callback);
		});
	}-*/;

}
