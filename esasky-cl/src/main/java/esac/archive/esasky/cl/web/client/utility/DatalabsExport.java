package esac.archive.esasky.cl.web.client.utility;

import com.github.nmorel.gwtjackson.client.ObjectMapper;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.shared.DateTimeFormat;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.model.entities.EsaSkyEntity;
import esac.archive.esasky.cl.web.client.model.entities.GeneralEntityInterface;
import esac.archive.esasky.cl.web.client.model.entities.ImageListEntity;
import esac.archive.esasky.cl.web.client.model.entities.MOCEntity;
import esac.archive.esasky.cl.web.client.model.entities.PublicationsByAuthorEntity;
import esac.archive.esasky.cl.web.client.model.entities.PublicationsBySourceEntity;
import esac.archive.esasky.cl.web.client.model.entities.PublicationsEntity;
import esac.archive.esasky.cl.web.client.model.entities.SSOEntity;
import esac.archive.esasky.cl.web.client.repository.EntityRepository;
import esac.archive.esasky.cl.web.client.utility.jupyter.Jupyter;
import esac.archive.esasky.cl.web.client.utility.jupyter.JupyterCell;
import esac.archive.esasky.cl.web.client.utility.jupyter.JupyterCodeCell;
import esac.archive.esasky.cl.web.client.utility.jupyter.JupyterMarkdownCell;
import esac.archive.esasky.cl.web.client.view.ctrltoolbar.selectsky.SelectSkyPanel;
import esac.archive.esasky.cl.web.client.view.ctrltoolbar.selectsky.SkyRow;
import esac.archive.esasky.cl.web.client.view.resultspanel.ITablePanel;
import esac.archive.esasky.ifcs.model.coordinatesutils.Coordinate;
import esac.archive.esasky.ifcs.model.descriptor.CommonTapDescriptor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static esac.archive.esasky.cl.web.client.utility.EsaSkyWebConstants.CATEGORY_PUBLICATIONS;

public class DatalabsExport {
    private static final List<String> OUR_EXTERNAL_TAPS = new ArrayList<>(Arrays.asList("ESO", "ASTRON", "HEASARC", "MAST"));

    public interface JupyterMapper extends ObjectMapper<Jupyter> {
    }

    public static void exportAllTables() {
        Jupyter jupyter = new Jupyter();
        addInstallationStep(jupyter);
        addWidgetStep(jupyter);
        addHipsStep(jupyter);

        jupyter.addCell(new JupyterMarkdownCell("## " + TextMgr.getInstance().getText("JupyterNotebook_code_block")));

        List<GeneralEntityInterface> entities = EntityRepository.getInstance().getAllEntities();
        double[] lastPosition = null;
        for (GeneralEntityInterface entity : entities) {
            double[] currentPosition = entityPosition(entity);
            if (!Arrays.equals(currentPosition, lastPosition)) {
                lastPosition = currentPosition;
                addGoToStep(jupyter, entity);
            }
            addEntityStep(jupyter, entity);
        }

        JupyterMapper mapper = GWT.create(JupyterMapper.class);
        downloadNotebook(mapper.write(jupyter));
    }

    public static void exportTablePanel(ITablePanel panel) {
        Jupyter jupyter = new Jupyter();
        GeneralEntityInterface entity = panel.getEntity();
        addInstallationStep(jupyter);
        addWidgetStep(jupyter);
        addHipsStep(jupyter);
        jupyter.addCell(new JupyterMarkdownCell("## " + TextMgr.getInstance().getText("JupyterNotebook_code_block")));
        addGoToStep(jupyter, entity);
        addEntityStep(jupyter, entity);

        JupyterMapper mapper = GWT.create(JupyterMapper.class);
        downloadNotebook(mapper.write(jupyter));
    }

    private static double[] entityPosition(GeneralEntityInterface entity) {
        Coordinate coordinate = entity.getSkyViewPosition().getCoordinate();
        return new double[]{coordinate.getRa(), coordinate.getDec(), entity.getSkyViewPosition().getFov()};
    }

    private static void addInstallationStep(Jupyter jupyter) {
        Date date = new Date();
        DateTimeFormat dtf = DateTimeFormat.getFormat(com.google.gwt.i18n.shared.DateTimeFormat.PredefinedFormat.ISO_8601);
        String currentTime = dtf.format(date);

        jupyter.addCell(new JupyterMarkdownCell(
                TextMgr.getInstance().getText("JupyterNotebook_welcome_text").replace("$TIMESTAMP$", currentTime))
        );
        jupyter.addCell(new JupyterMarkdownCell(
                "## " + TextMgr.getInstance().getText("JupyterNotebook_import_libraries")
        ));
        JupyterCodeCell installationCell = new JupyterCodeCell(
                "%pip install astroquery --upgrade",
                "%pip install pandas",
                "%pip install pyesasky --upgrade",
                "from astroquery.esasky import ESASky",
                "from astroquery.utils.tap.core import TapPlus",
                "from astroquery.simbad import Simbad",
                "import pandas as pd",
                "from pyesasky import ESASkyWidget"
        );
        jupyter.addCell(installationCell);
    }

    private static void addWidgetStep(Jupyter jupyter) {
        jupyter.addCell(new JupyterMarkdownCell(
                "## " + TextMgr.getInstance().getText("JupyterNotebook_esasky_widget")
        ));
        jupyter.addCell(new JupyterCodeCell(
                "esasky = ESASkyWidget()",
                "esasky"));
    }

    private static void addHipsStep(Jupyter jupyter) {
        jupyter.addCell(new JupyterMarkdownCell(
                "## " + TextMgr.getInstance().getText("JupyterNotebook_configure_view")
        ));
        JupyterCodeCell hipsCell = new JupyterCodeCell();

        SelectSkyPanel skyPanel = SelectSkyPanel.getInstance();
        List<SkyRow> hips = skyPanel.getHipsList();

        for (int i = 0; i < hips.size(); i++) {
            String hipsName = hips.get(i).getSelectedHips().getSurveyName();
            if (i == 0) {
                hipsCell.addLine("esasky.select_hips('" + hipsName + "')");
            } else {
                hipsCell.addLine("esasky.add_hips('" + hipsName + "')");
            }
        }
        jupyter.addCell(hipsCell);

    }

    private static void addGoToStep(Jupyter jupyter, GeneralEntityInterface entity) {
        Coordinate coordinate = entity.getSkyViewPosition().getCoordinate();
        JupyterCell goToCell = new JupyterCodeCell(
                "esasky.go_to('" + coordinate.getRa() + "', '" + coordinate.getDec() + "')",
                "esasky.set_fov(" + entity.getSkyViewPosition().getFov() + ")"
        );
        jupyter.addCell(goToCell);
    }

    private static void addEntityStep(Jupyter jupyter, GeneralEntityInterface entity) {
        if (CATEGORY_PUBLICATIONS.equals(entity.getDescriptor().getCategory())) {
            return;
        } else if (entity.getDescriptor().isExternal() && OUR_EXTERNAL_TAPS.contains(entity.getDescriptor().getMission())) {
            addOurExternalTapStep(jupyter, entity);
        } else if (entity.getDescriptor().isExternal()) {
            addExternalTapStep(jupyter, entity);
        } else if (entity instanceof EsaSkyEntity || entity instanceof MOCEntity) {
            addRegularTapStep(jupyter, entity);
        }
    }

    private static void addOurExternalTapStep(Jupyter jupyter, GeneralEntityInterface entity) {
        CommonTapDescriptor descriptor = entity.getDescriptor();
        String description = descriptor.getCategory() + " " + descriptor.getShortName();

        String levelDescriptor = descriptor.getShortName();
        descriptor = descriptor.getParent();
        while (descriptor != null) {
            levelDescriptor = descriptor.getShortName() + "-" + levelDescriptor;
            descriptor = descriptor.getParent();
        }

        jupyter.addCell(new JupyterCodeCell(
                "# " + description,
                "esasky.get_tap_count('" + entity.getDescriptor().getMission() + "')",
                "esasky.plot_tap('" + levelDescriptor + "')"));
        jupyter.addCell(new JupyterCodeCell(
                "data=esasky.get_result_data()",
                "data = data if type(data) is list else [data]",
                "data_frame=pd.DataFrame.from_dict(data)",
                "data_frame"));
    }

    private static void addExternalTapStep(Jupyter jupyter, GeneralEntityInterface entity) {
        CommonTapDescriptor descriptor = entity.getDescriptor();
        String description = descriptor.getCategory() + " " + descriptor.getShortName();
        String tapUrl = getExternalTapUrl(descriptor);
        jupyter.addCell(new JupyterCodeCell(
                "#" + TextMgr.getInstance().getText("Loading data") + " " + description,
                "query = \"" + entity.getQuery() + "\"",
                "tap_url = \"" + tapUrl + "\"",
                "tap = TapPlus(url=tap_url)",
                "job = tap.launch_job(query)",
                "data = job.get_data()"));

        jupyter.addCell(new JupyterCodeCell(
                "def column_name_with_ucds(columns, ucds):",
                "    for column_name in columns:",
                "        words = columns[column_name].meta.get('ucd', '').split(';')",
                "        if ucds.issubset(words):",
                "            return column_name",
                "    return None",
                "",
                "def find_first_matching(columns, list_of_sets_of_ucds):",
                "    for ucd_set in list_of_sets_of_ucds:",
                "        column_name = column_name_with_ucds(columns, ucd_set)",
                "        if column_name:",
                "            return column_name",
                "    return None"));
        jupyter.addCell(new JupyterCodeCell(
                "# " + TextMgr.getInstance().getText("JupyterNotebook_try_finding_columns"),
                "ra_column_name = find_first_matching(data.columns, [{'pos.eq.ra', 'meta.main'}, {'pos.eq.ra'}])",
                "dec_column_name = find_first_matching(data.columns, [{'pos.eq.dec', 'meta.main'}, {'pos.eq.dec'}])",
                "id_column_name = find_first_matching(data.columns, [{'meta.id', 'meta.main'}, {'meta.id'}])"
        ));
        jupyter.addCell(new JupyterCodeCell(
                "data"));
        jupyter.addCell(new JupyterCodeCell(
                "# " + TextMgr.getInstance().getText("JupyterNotebook_add_data_points"),
                "esasky.overlay_cat_astropy(\"" + descriptor.getShortName() + "\", \"J2000\", \"#a343ff\", 7, data, ra_column_name, dec_column_name, id_column_name)"
        ));
    }

    private static void addRegularTapStep(Jupyter jupyter, GeneralEntityInterface entity) {
        CommonTapDescriptor descriptor = entity.getDescriptor();
        String plotData;
        switch (descriptor.getCategory()) {
            case "observations":
                plotData = "esasky.plot_obs('" + descriptor.getMission() + "')";
                break;
            case "spectra":
                plotData = "esasky.plot_spec('" + descriptor.getMission() + "')";
                break;
            case "catalogues":
                plotData = "esasky.plot_cat('" + descriptor.getMission() + "')";
                break;
            default:
                plotData = "";
        }
        jupyter.addCell(new JupyterCodeCell(
                plotData,
                "data=esasky.get_result_data()",
                "data = data if type(data) is list else [data]",
                "data_frame=pd.DataFrame.from_dict(data)",
                "data_frame"));
    }

    private static String getExternalTapUrl(CommonTapDescriptor descriptor) {
        String tapUrl = descriptor.getOriginalParent().getTapUrl();
        if (tapUrl.endsWith("sync")) {
            tapUrl = tapUrl.replace("/sync", "");
        }
        return tapUrl;
    }


    public static boolean supportsJupyterDownload(final GeneralEntityInterface entity) {
        return entity instanceof EsaSkyEntity &&
                !(entity instanceof ImageListEntity ||
                        entity instanceof PublicationsEntity ||
                        entity instanceof SSOEntity ||
                        entity instanceof PublicationsBySourceEntity ||
                        entity instanceof PublicationsByAuthorEntity);
    }

    private static native void downloadNotebook(String content) /*-{
        var uri = 'data:application/x-ipynb+json;charset=utf-8,' + content;

        var downloadLink = document.createElement("a");
        downloadLink.href = uri;
        var file = new Blob([content], {type: 'appliction/xml;charset=utf-8'})
        downloadLink.href = URL.createObjectURL(file);
        var date = new Date().toLocaleDateString();
        var time = new Date().toLocaleTimeString();
        downloadLink.download = "ESASky export " + date + " " + time + ".ipynb";

        document.body.appendChild(downloadLink);
        downloadLink.click();
        document.body.removeChild(downloadLink);

        URL.revokeObjectURL(downloadLink.href);
    }-*/;
}
