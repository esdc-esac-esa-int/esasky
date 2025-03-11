package esac.archive.absi.modules.cl.aladinlite.widget.client;

import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

import esac.archive.absi.modules.cl.aladinlite.widget.client.model.ColorPalette;

public interface AladinLiteCDSWrapper {

    void goToObject(String inputObjectName);

    void setZoom(double zoomDegrees);

    void displayJPG(String imageURL, String transparency);

    void setRainbowColorMap(String layerId);

    void setEosbColorMap(String layerId);

    void setNativeColorMap(String layerId);

    void setGrayscaleColorMap(String layerId);

    void setPlanckColorMap(String layerId);

    void setCMBColorMap(String layerId);
    void setCubehelixColorMap(String layerId);

    JavaScriptObject createJ2000Polyline(double [] polylineDouble);

    void addJ2000PolylineToOverlay(JavaScriptObject overlayJsObject, JavaScriptObject polyline);
    void removeJ2000PolylineFromOverlay(JavaScriptObject overlayJsObject, JavaScriptObject polyline);

    JavaScriptObject createAndSetImageSurveyWithImgFormat(String skyRowId, String surveyId, String surveyName,
            String surveyRootUrl, String surveyFrame, int maximumNorder, String imgFormat, String colormap, boolean shouldUseCredentials);

    JavaScriptObject getCurrentImageSurveyObject();

    void addCatalogToAladin(JavaScriptObject catalog);

    void setColorPalette(String layerId, ColorPalette colorPalette);

    void reverseColorMap();


}
