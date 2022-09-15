package esac.archive.esasky.ifcs.model.shared;

import java.util.List;
import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.LinkedList;

import esac.archive.absi.modules.cl.aladinlite.widget.client.model.ColorPalette;
import esac.archive.esasky.ifcs.model.client.HiPS.HiPSImageFormat;
import esac.archive.esasky.ifcs.model.client.HipsWavelength;

/**
 * @author ESDC team Copyright (c) 2015- European Space Agency
 */
public class EsaSkyConstants {


    /*************************************************************************/
    /** GENERAL CONSTANTS **/
    /*************************************************************************/

    /** Replace pattern. */
    public static final String REPLACE_PATTERN = "@@";

    /** App name. */
    public static final String APP_NAME = "ESASky";

    /** Server Context. */
    public static final String PROP_SERVER_CONTEXT = "server.context";
    /** Tap Context. */
    public static final String PROP_TAP_CONTEXT = "tap.context";

    /** Not target found. */
    public static final String NOT_TARGET_FOUND = "No astronomical object found";

    /** Default FoV. */
    public static final Double DEFAULT_FOV = 0.1; // 6 arcmin
    
    /** Default FoV. */
    public static final Double MIN_ALLOWED_DEFAULT_FOV = 0.09; // < 6 arcmin

    /** JSON format. */
    public static final String JSON = "json";

    /** VOTABLE format. */
    public static final String VOTABLE = "votable";

    public static final String INTERNATIONALIZATION_LANGCODE_URL_PARAM = "lang";
    
    
    

    /*************************************************************************/
    /** DEFAULT INIT VALUES **/
    /*************************************************************************/

    /** AladinLite ID name */
    public static final String ALADIN_DIV_NAME = "aladin-container";

    /** Default Hips map. */
    public static final String ALADIN_DEFAULT_HIPS_MAP = "DSS2 color";
    /** Default survey Id. */
    public static final String ALADIN_DEFAULT_SURVEY_ID = "DSS2 Color";
    /** Default survey name. */
    public static final String ALADIN_DEFAULT_SURVEY_NAME = "DSS2 color";
    /** Default survey name. */
    public static final HipsWavelength DEFAULT_WAVELENGTH = HipsWavelength.OPTICAL;
    /** Default survey URL. */
    public static final String ALADIN_DEFAULT_SURVEY_URL = "//cdn.skies.esac.esa.int/DSSColor";
    /** Default HiPS cooframe. */
    public static final String ALADIN_HiPS_DEFAULT_COO_FRAME = "equatorial";
    /** Default Hips Image format. */
    public static final HiPSImageFormat ALADIN_DEFAULT_IMG_FORMAT = HiPSImageFormat.jpg;
    /** Default color palette. */
    public static final ColorPalette ALADIN_DEFAULT_COLOR_MAP = ColorPalette.NATIVE;
    /** Default norder. */
    public static final Integer ALADIN_DEFAULT_NORDER = 9;
    /** Default target. */
    public static final String ALADIN_DEFAULT_TARGET = "M51";
    /** Default FoV value. */
    public static final double initFoV = 1.0;

    /*************************************************************************/
    /** Patterns used to validate RA/DEC format **/
    /*************************************************************************/

    private static final String BLANK = "\\s*";

    private static final String SIGN = "([+-])?";

    private static final String BEGIN = "^" + BLANK;

    private static final String END = BLANK + "$";

    // Atomic elements
    private static final String HOUR = "([0-2]?[0-9])";

    private static final String MIN = "([0-5]?[0-9])";

    private static final String SEC = "([0-5]?[0-9]\\.?\\d*)";

    private static final String SEP2 = "[\\s\\:\\,]+";

    private static final String DEG = "(\\d{1,2})";

    private static final String HOUR_UNIT = "(?:([0-2]?[0-9])h)?";

    private static final String MIN_UNIT = "(?:([0-5]?[0-9])m)?";

    private static final String SEC_UNIT = "(?:([0-5]?[0-9]\\.?\\d*)s)?";

    private static final String DEG_UNIT = "(?:(\\d{1,2})d)?";

    private static final String AMIN_UNIT = "(?:([0-5]?[0-9])[m\\'])?";

    private static final String ASEC_UNIT = "(?:([0-5]?[0-9]\\.?\\d*)(?:s|\\'{2}))?";

    /** RA format: 1, 2 or 3 numbers without units or any with them. */
    public static final String RA_DECIMAL_PATTERN = BEGIN + "([\\d\\.]+)([dh])?" + END;

    public static final String RA_TWO_NUMBERS_PATTERN = BEGIN + HOUR + SEP2 + MIN + END;

    public static final String RA_THREE_NUMBERS_PATTERN = BEGIN + HOUR + SEP2 + MIN + SEP2 + SEC
            + END;

    public static final String RA_UNITS_PATTERN = BEGIN + HOUR_UNIT + BLANK + MIN_UNIT + BLANK
            + SEC_UNIT + END;

    /** DEC format: 1, 2 or 3 numbers without units or any with them. */
    public static final String DEC_DECIMAL_PATTERN = BEGIN + SIGN + BLANK + "([\\d\\.]+)d?" + END;

    public static final String DEC_TWO_NUMBERS_PATTERN = BEGIN + SIGN + BLANK + DEG + SEP2 + MIN
            + END;

    public static final String DEC_THREE_NUMBERS_PATTERN = BEGIN + SIGN + BLANK + DEG + SEP2 + MIN
            + SEP2 + SEC + END;

    public static final String DEC_UNITS_PATTERN = BEGIN + SIGN + DEG_UNIT + BLANK + AMIN_UNIT
            + BLANK + ASEC_UNIT + END;
    /** String starts with character. */
    public static final String TARGET_NAME = BEGIN + "[a-zA-Z,-]+";

    /*************************************************************************/
    /** View Constants **/
    /*************************************************************************/

    /** Buttons Standard width. */
    public static final String BUTTONS_STANDARD_WIDTH = "145px";

    /** Type of button. */
    public static final String SAVE_BUTTON_TYPE = "save";
    /** Type of button. */
    public static final String SEND_BUTTON_TYPE = "send";
    /** Max catalogue search size. */
    public static final Integer MAX_CATALOG_SEARCH_SIZE = 50000;
    /** FoV multiplier for multi-target player. */
    public static final double MULTITARGET_FOV = 2.0;

    /** Default HiPS cooframe. */
    public static final String SIMBAD_J2000_COO_FRAME = "ICRS";
    /** Default HiPS cooframe. */
    public static final String SIMBAD_GALACTIC_COO_FRAME = "Gal";

    public static final String SIMBAD_OBJECT_NOT_IDENTIFIED = "No astronomical object found";

    /** USER ERROR MESSAGES */
    public static final String ERROR_MSG_TOO_MANY_USERS = "The server is taking too long to answer, sorry. "
            + "Please, either submit again your request or refine your search. "
            + "It could be that in this moment there are many users connected.";

    public static final String ERROR_MSG_UPLOAD_FILE = "The server is taking too long to answer, sorry. "
            + "Please, either try to submit again your file. "
            + "It could be that in this moment there are many users connected.";

    public static final String ERROR_MSG_SIMBAD = "Sorry for the inconvinience, "
            + "but it seems that the connection between our servers and SIMABD "
            + "resolver service has some problems. Please retry your search.";

    public static final String ERROR_MSG_SAMP = "Something went wrong in the comunication between SAMP applications. Please retry your search and if the problem persists, tell us using either the userecho page or the helpdesk.";


    public static final String DATALINK_URL_PARAM = "url";
    public static final String IMAGELOADER_URL_PARAM = "url";
    public static final String HST_IMAGE_ID_PARAM = "id";
    public static final String HST_IMAGE_ACTION_PARAM = "action";
    public static final String HST_MISSION = "HST";

    /*************************************************************************/
    /** INTERNATIONALIZATION VALUES **/
    /*************************************************************************/
    public static final String DEFAULT_LANGCODE = "en"; 
    private static final List<SimpleEntry<String, String>> AVAILABLE_LANGUAGES = new LinkedList<SimpleEntry<String, String>>(
    		Arrays.asList(
    				new SimpleEntry<String, String>("en", "En"),
    				new SimpleEntry<String, String>("es", "Es")
    				,new SimpleEntry<String, String>("zh", "中文")
    				)
    		);
    
    /*************************************************************************/
    /** COMMUNICATION CONSTANTS **/
    /*************************************************************************/
    
    public static final String EXT_TAP_ACTION_FLAG = "ACTION";
    public static final String EXT_TAP_TARGET_FLAG = "TAP_TARGET";
    public static final String EXT_TAP_ADQL_FLAG = "ADQL";
    public static final String EXT_TAP_ACTION_DESCRIPTORS = "DESCRIPTORS";
    public static final String EXT_TAP_ACTION_RESET = "RESET";
    public static final String EXT_TAP_ACTION_REQUEST = "REQUEST";
    public static final String EXT_TAP_URL = "TAP_URL";
    public static final String EXT_TAP_RESPONSE_FORMAT = "RESPONSE_FORMAT";

    
    /*************************************************************************/
    /** IVOA OBSCORE **/
    /*************************************************************************/
    
    public static final String OBSCORE_COLLECTION = "obs_collection";
    public static final String OBSCORE_FACILITY = "facility_name";
    public static final String OBSCORE_DATAPRODUCT = "dataproduct_type";
    public static final String OBSCORE_FOV = "s_fov";
    public static final String OBSCORE_SREGION = "s_region";
    public static final String CATALOGUE = "catalogue";
    public static final String TABLE_NAME = "table_name";
    public static final String HEASARC_TABLE = "table_name";

    /*************************************************************************/
    /** IVOA UCD **/
    /*************************************************************************/

    public static final String UCD_ARITH = "arith"; //  Arithmetic quantities
    public static final String UCD_ARITH_DIFF = "arith.diff"; //  Difference between two quantities described by the same UCD
    public static final String UCD_ARITH_FACTOR = "arith.factor"; //  Numerical factor
    public static final String UCD_ARITH_GRAD = "arith.grad"; //  Gradient
    public static final String UCD_ARITH_RATE = "arith.rate"; //  Rate (per time unit)
    public static final String UCD_ARITH_RATIO = "arith.ratio"; //  Ratio between two quantities described by the same UCD
    public static final String UCD_ARITH_SQUARED = "arith.squared"; //  Squared quantity
    public static final String UCD_ARITH_SUM = "arith.sum"; //  Summed or integrated quantity
    public static final String UCD_ARITH_VARIATION = "arith.variation"; //  Generic variation of a quantity
    public static final String UCD_ARITH_ZP = "arith.zp"; //  Zero point
    public static final String UCD_EM = "em"; //  Electromagnetic spectrum
    public static final String UCD_EM_IR = "em.IR"; //  Infrared part of the spectrum
    public static final String UCD_EM_IR_J = "em.IR.J"; //  Infrared between 1.0 and 1.5 micron
    public static final String UCD_EM_IR_H = "em.IR.H"; //  Infrared between 1.5 and 2 micron
    public static final String UCD_EM_IR_K = "em.IR.K"; //  Infrared between 2 and 3 micron
    public static final String UCD_EM_IR_3_4UM = "em.IR.3-4um"; //  Infrared between 3 and 4 micron
    public static final String UCD_EM_IR_4_8UM = "em.IR.4-8um"; //  Infrared between 4 and 8 micron
    public static final String UCD_EM_IR_8_15UM = "em.IR.8-15um"; //  Infrared between 8 and 15 micron
    public static final String UCD_EM_IR_15_30UM = "em.IR.15-30um"; //  Infrared between 15 and 30 micron
    public static final String UCD_EM_IR_30_60UM = "em.IR.30-60um"; //  Infrared between 30 and 60 micron
    public static final String UCD_EM_IR_60_100UM = "em.IR.60-100um"; //  Infrared between 60 and 100 micron
    public static final String UCD_EM_IR_NIR = "em.IR.NIR"; //  Near-Infrared, 1-5 microns
    public static final String UCD_EM_IR_MIR = "em.IR.MIR"; //  Medium-Infrared, 5-30 microns
    public static final String UCD_EM_IR_FIR = "em.IR.FIR"; //  Far-Infrared, 30-100 microns
    public static final String UCD_EM_UV = "em.UV"; //  Ultraviolet part of the spectrum
    public static final String UCD_EM_UV_10_50NM = "em.UV.10-50nm"; //  Ultraviolet between 10 and 50 nm EUV extreme UV
    public static final String UCD_EM_UV_50_100NM = "em.UV.50-100nm"; //  Ultraviolet between 50 and 100 nm
    public static final String UCD_EM_UV_100_200NM = "em.UV.100-200nm"; //  Ultraviolet between 100 and 200 nm FUV Far UV
    public static final String UCD_EM_UV_200_300NM = "em.UV.200-300nm"; //  Ultraviolet between 200 and 300 nm NUV near UV
    public static final String UCD_EM_X_RAY = "em.X-ray"; //  X-ray part of the spectrum
    public static final String UCD_EM_X_RAY_SOFT = "em.X-ray.soft"; //  Soft X-ray (0.12 - 2 keV)
    public static final String UCD_EM_X_RAY_MEDIUM = "em.X-ray.medium"; //  Medium X-ray (2 - 12 keV)
    public static final String UCD_EM_X_RAY_HARD = "em.X-ray.hard"; //  Hard X-ray (12 - 120 keV)
    public static final String UCD_EM_BIN = "em.bin"; //  Channel / instrumental spectral bin coordinate (bin number)
    public static final String UCD_EM_ENERGY = "em.energy"; //  Energy value in the em frame
    public static final String UCD_EM_FREQ = "em.freq"; //  Frequency value in the em frame
    public static final String UCD_EM_FREQ_CUTOFF = "em.freq.cutoff"; //  cutoff frequency
    public static final String UCD_EM_FREQ_RESONANCE = "em.freq.resonance"; //  resonance frequency
    public static final String UCD_EM_GAMMA = "em.gamma"; //  Gamma rays part of the spectrum
    public static final String UCD_EM_GAMMA_SOFT = "em.gamma.soft"; //  Soft gamma ray (120 - 500 keV)
    public static final String UCD_EM_GAMMA_HARD = "em.gamma.hard"; //  Hard gamma ray (>500 keV)
    public static final String UCD_EM_LINE = "em.line"; //  Designation of major atomic lines
    public static final String UCD_EM_LINE_HI = "em.line.HI"; //  21cm hydrogen line
    public static final String UCD_EM_LINE_LYALPHA = "em.line.Lyalpha"; //  H-Lyalpha line
    public static final String UCD_EM_LINE_HALPHA = "em.line.Halpha"; //  H-alpha line
    public static final String UCD_EM_LINE_HBETA = "em.line.Hbeta"; //  H-beta line
    public static final String UCD_EM_LINE_HGAMMA = "em.line.Hgamma"; //  H-gamma line
    public static final String UCD_EM_LINE_HDELTA = "em.line.Hdelta"; //  H-delta line
    public static final String UCD_EM_LINE_BRGAMMA = "em.line.Brgamma"; //  Bracket gamma line
    public static final String UCD_EM_LINE_CO = "em.line.CO"; //  CO radio line, e.g. 12CO(1-0) at 115GHz
    public static final String UCD_EM_LINE_OIII = "em.line.OIII"; //  [OIII] line whose rest wl is 500.7 nm
    public static final String UCD_EM_MM = "em.mm"; //  Millimetric/submillimetric part of the spectrum
    public static final String UCD_EM_MM_30_50GHZ = "em.mm.30-50GHz"; //  Millimetric between 30 and 50 GHz
    public static final String UCD_EM_MM_50_100GHZ = "em.mm.50-100GHz"; //  Millimetric between 50 and 100 GHz
    public static final String UCD_EM_MM_100_200GHZ = "em.mm.100-200GHz"; //  Millimetric between 100 and 200 GHz
    public static final String UCD_EM_MM_200_400GHZ = "em.mm.200-400GHz"; //  Millimetric between 200 and 400 GHz
    public static final String UCD_EM_MM_400_750GHZ = "em.mm.400-750GHz"; //  Millimetric between 400 and 750 GHz
    public static final String UCD_EM_MM_750_1500GHZ = "em.mm.750-1500GHz"; //  Millimetric between 750 and 1500 GHz
    public static final String UCD_EM_MM_1500_3000GHZ = "em.mm.1500-3000GHz"; //  Millimetric between 1500 and 3000 GHz
    public static final String UCD_EM_OPT = "em.opt"; //  Optical part of the spectrum
    public static final String UCD_EM_OPT_U = "em.opt.U"; //  Optical band between 300 and 400 nm
    public static final String UCD_EM_OPT_B = "em.opt.B"; //  Optical band between 400 and 500 nm
    public static final String UCD_EM_OPT_V = "em.opt.V"; //  Optical band between 500 and 600 nm
    public static final String UCD_EM_OPT_R = "em.opt.R"; //  Optical band between 600 and 750 nm
    public static final String UCD_EM_OPT_I = "em.opt.I"; //  Optical band between 750 and 1000 nm
    public static final String UCD_EM_PW = "em.pw"; //  Plasma waves (trapped in local medium)
    public static final String UCD_EM_RADIO = "em.radio"; //  Radio part of the spectrum
    public static final String UCD_EM_RADIO_20MHZ = "em.radio.20MHz"; //  Radio below 20 MHz
    public static final String UCD_EM_RADIO_20_100MHZ = "em.radio.20-100MHz"; //  Radio between 20 and 100 MHz
    public static final String UCD_EM_RADIO_100_200MHZ = "em.radio.100-200MHz"; //  Radio between 100 and 200 MHz
    public static final String UCD_EM_RADIO_200_400MHZ = "em.radio.200-400MHz"; //  Radio between 200 and 400 MHz
    public static final String UCD_EM_RADIO_400_750MHZ = "em.radio.400-750MHz"; //  Radio between 400 and 750 MHz
    public static final String UCD_EM_RADIO_750_1500MHZ = "em.radio.750-1500MHz"; //  Radio between 750 and 1500 MHz
    public static final String UCD_EM_RADIO_1500_3000MHZ = "em.radio.1500-3000MHz"; //  Radio between 1500 and 3000 MHz
    public static final String UCD_EM_RADIO_3_6GHZ = "em.radio.3-6GHz"; //  Radio between 3 and 6 GHz
    public static final String UCD_EM_RADIO_6_12GHZ = "em.radio.6-12GHz"; //  Radio between 6 and 12 GHz
    public static final String UCD_EM_RADIO_12_30GHZ = "em.radio.12-30GHz"; //  Radio between 12 and 30 GHz
    public static final String UCD_EM_WAVENUMBER = "em.wavenumber"; //  Wavenumber value in the em frame
    public static final String UCD_EM_WL = "em.wl"; //  Wavelength value in the em frame
    public static final String UCD_EM_WL_CENTRAL = "em.wl.central"; //  Central wavelength
    public static final String UCD_EM_WL_EFFECTIVE = "em.wl.effective"; //  Effective wavelength
    public static final String UCD_INSTR = "instr"; //  Instrument
    public static final String UCD_INSTR_BACKGROUND = "instr.background"; //  Instrumental background
    public static final String UCD_INSTR_BANDPASS = "instr.bandpass"; //  Bandpass (e.g.: band name) of instrument
    public static final String UCD_INSTR_BANDWIDTH = "instr.bandwidth"; //  Bandwidth of the instrument
    public static final String UCD_INSTR_BASELINE = "instr.baseline"; //  Baseline for interferometry
    public static final String UCD_INSTR_BEAM = "instr.beam"; //  Beam
    public static final String UCD_INSTR_CALIB = "instr.calib"; //  Calibration parameter
    public static final String UCD_INSTR_DET = "instr.det"; //  Detector
    public static final String UCD_INSTR_DET_NOISE = "instr.det.noise"; //  Instrument noise
    public static final String UCD_INSTR_DET_PSF = "instr.det.psf"; //  Point Spread Function
    public static final String UCD_INSTR_DET_QE = "instr.det.qe"; //  Quantum efficiency
    public static final String UCD_INSTR_DISPERSION = "instr.dispersion"; //  Dispersion of a spectrograph
    public static final String UCD_INSTR_EXPERIMENT = "instr.experiment"; //  Experiment or group of instruments
    public static final String UCD_INSTR_FILTER = "instr.filter"; //  Filter
    public static final String UCD_INSTR_FOV = "instr.fov"; //  Field of view
    public static final String UCD_INSTR_OBSTY = "instr.obsty"; //  Observatory, satellite, mission
    public static final String UCD_INSTR_OBSTY_SEEING = "instr.obsty.seeing"; //  Seeing
    public static final String UCD_INSTR_OFFSET = "instr.offset"; //  Offset angle respect to main direction of observation
    public static final String UCD_INSTR_ORDER = "instr.order"; //  Spectral order in a spectrograph
    public static final String UCD_INSTR_PARAM = "instr.param"; //  Various instrumental parameters
    public static final String UCD_INSTR_PIXEL = "instr.pixel"; //  Pixel (default size: angular)
    public static final String UCD_INSTR_PLATE = "instr.plate"; //  Photographic plate
    public static final String UCD_INSTR_PLATE_EMULSION = "instr.plate.emulsion"; //  Plate emulsion
    public static final String UCD_INSTR_PRECISION = "instr.precision"; //  Instrument precision
    public static final String UCD_INSTR_RMSF = "instr.rmsf"; //  Rotation Measure Spread Function
    public static final String UCD_INSTR_SATURATION = "instr.saturation"; //  Instrument saturation threshold
    public static final String UCD_INSTR_SCALE = "instr.scale"; //  Instrument scale (for CCD, plate, image)
    public static final String UCD_INSTR_SENSITIVITY = "instr.sensitivity"; //  Instrument sensitivity, detection threshold
    public static final String UCD_INSTR_SETUP = "instr.setup"; //  Instrument configuration or setup
    public static final String UCD_INSTR_SKYLEVEL = "instr.skyLevel"; //  Sky level
    public static final String UCD_INSTR_SKYTEMP = "instr.skyTemp"; //  Sky temperature
    public static final String UCD_INSTR_TEL = "instr.tel"; //  Telescope
    public static final String UCD_INSTR_TEL_FOCALLENGTH = "instr.tel.focalLength"; //  Telescope focal length
    public static final String UCD_INSTR_VOXEL = "instr.voxel"; //  Related to a voxel (n-D volume element with n>2)
    public static final String UCD_META = "meta"; //  Metadata
    public static final String UCD_META_ABSTRACT = "meta.abstract"; //  Abstract (of paper, proposal, etc.)
    public static final String UCD_META_BIB = "meta.bib"; //  Bibliographic reference
    public static final String UCD_META_BIB_AUTHOR = "meta.bib.author"; //  Author name
    public static final String UCD_META_BIB_BIBCODE = "meta.bib.bibcode"; //  Bibcode
    public static final String UCD_META_BIB_FIG = "meta.bib.fig"; //  Figure in a paper
    public static final String UCD_META_BIB_JOURNAL = "meta.bib.journal"; //  Journal name
    public static final String UCD_META_BIB_PAGE = "meta.bib.page"; //  Page number
    public static final String UCD_META_BIB_VOLUME = "meta.bib.volume"; //  Volume number
    public static final String UCD_META_CALIBLEVEL = "meta.calibLevel"; //  Processing/calibration level
    public static final String UCD_META_CODE = "meta.code"; //  Code or flag
    public static final String UCD_META_CODE_CLASS = "meta.code.class"; //  Classification code
    public static final String UCD_META_CODE_ERROR = "meta.code.error"; //  Limit uncertainty error flag
    public static final String UCD_META_CODE_MEMBER = "meta.code.member"; //  Membership code
    public static final String UCD_META_CODE_MIME = "meta.code.mime"; //  MIME type
    public static final String UCD_META_CODE_MULTIP = "meta.code.multip"; //  Multiplicity or binarity flag
    public static final String UCD_META_CODE_QUAL = "meta.code.qual"; //  Quality, precision, reliability flag or code
    public static final String UCD_META_CODE_STATUS = "meta.code.status"; //  Status code (e.g.: status of a proposal/observation)
    public static final String UCD_META_CRYPTIC = "meta.cryptic"; //  Unknown or impossible to understand quantity
    public static final String UCD_META_CURATION = "meta.curation"; //  Identity of man/organization responsible for the data
    public static final String UCD_META_DATASET = "meta.dataset"; //  Dataset
    public static final String UCD_META_EMAIL = "meta.email"; //  Curation/contact e-mail
    public static final String UCD_META_FILE = "meta.file"; //  File
    public static final String UCD_META_FITS = "meta.fits"; //  FITS standard
    public static final String UCD_META_ID = "meta.id"; //  Identifier, name or designation
    public static final String UCD_META_ID_ASSOC = "meta.id.assoc"; //  Identifier of associated counterpart
    public static final String UCD_META_ID_COI = "meta.id.CoI"; //  Name of Co-Investigator
    public static final String UCD_META_ID_CROSS = "meta.id.cross"; //  Cross identification
    public static final String UCD_META_ID_PARENT = "meta.id.parent"; //  Identification of parent source
    public static final String UCD_META_ID_PART = "meta.id.part"; //  Part of identifier, suffix or sub-component
    public static final String UCD_META_ID_PI = "meta.id.PI"; //  Name of Principal Investigator or Co-PI
    public static final String UCD_META_MAIN = "meta.main"; //  Main value of something
    public static final String UCD_META_MODELLED = "meta.modelled"; //  Quantity was produced by a model
    public static final String UCD_META_NOTE = "meta.note"; //  Note or remark (longer than a code or flag)
    public static final String UCD_META_NUMBER = "meta.number"; //  Number (of things; e.g. nb of object in an image)
    public static final String UCD_META_RECORD = "meta.record"; //  Record number
    public static final String UCD_META_PREVIEW = "meta.preview"; //  Related to a preview operation for a dataset
    public static final String UCD_META_QUERY = "meta.query"; //  A query posed to an information system or database or a property of it
    public static final String UCD_META_REF = "meta.ref"; //  Reference or origin
    public static final String UCD_META_REF_DOI = "meta.ref.doi"; //  DOI identifier (dereferenceable)
    public static final String UCD_META_REF_IVOID = "meta.ref.ivoid"; //  Related to an identifier as recommended in the IVOA (dereferenceable)
    public static final String UCD_META_REF_URI = "meta.ref.uri"; //  URI, universal resource identifier
    public static final String UCD_META_REF_URL = "meta.ref.url"; //  URL, web address
    public static final String UCD_META_SOFTWARE = "meta.software"; //  Software used in generating data
    public static final String UCD_META_TABLE = "meta.table"; //  Table or catalogue
    public static final String UCD_META_TITLE = "meta.title"; //  Title or explanation
    public static final String UCD_META_UCD = "meta.ucd"; //  UCD
    public static final String UCD_META_UNIT = "meta.unit"; //  Unit
    public static final String UCD_META_VERSION = "meta.version"; //  Version
    public static final String UCD_OBS = "obs"; //  Observation
    public static final String UCD_OBS_AIRMASS = "obs.airMass"; //  Airmass
    public static final String UCD_OBS_ATMOS = "obs.atmos"; //  Atmosphere, atmospheric phenomena affecting an observation
    public static final String UCD_OBS_ATMOS_EXTINCTION = "obs.atmos.extinction"; //  Atmospheric extinction
    public static final String UCD_OBS_ATMOS_REFRACTANGLE = "obs.atmos.refractAngle"; //  Atmospheric refraction angle
    public static final String UCD_OBS_CALIB = "obs.calib"; //  Calibration observation
    public static final String UCD_OBS_CALIB_FLAT = "obs.calib.flat"; //  Related to flat-field calibration observation (dome, sky, ..)
    public static final String UCD_OBS_CALIB_DARK = "obs.calib.dark"; //  Related to dark current calibration
    public static final String UCD_OBS_EXPOSURE = "obs.exposure"; //  Exposure
    public static final String UCD_OBS_FIELD = "obs.field"; //  Region covered by the observation
    public static final String UCD_OBS_IMAGE = "obs.image"; //  Image
    public static final String UCD_OBS_OBSERVER = "obs.observer"; //  Observer, discoverer
    public static final String UCD_OBS_OCCULT = "obs.occult"; //  Observation of occultation phenomenon by solar system objects
    public static final String UCD_OBS_TRANSIT = "obs.transit"; //  Observation of transit phenomenon  : exo-planets
    public static final String UCD_OBS_PARAM = "obs.param"; //  Various observation or reduction parameter
    public static final String UCD_OBS_PROPOSAL = "obs.proposal"; //  Observation proposal
    public static final String UCD_OBS_PROPOSAL_CYCLE = "obs.proposal.cycle"; //  Proposal cycle
    public static final String UCD_OBS_SEQUENCE = "obs.sequence"; //  Sequence of observations, exposures or events
    public static final String UCD_PHOT = "phot"; //  Photometry
    public static final String UCD_PHOT_ANTENNATEMP = "phot.antennaTemp"; //  Antenna temperature
    public static final String UCD_PHOT_CALIB = "phot.calib"; //  Photometric calibration
    public static final String UCD_PHOT_COLOR = "phot.color"; //  Color index or magnitude difference
    public static final String UCD_PHOT_COLOR_EXCESS = "phot.color.excess"; //  Color excess
    public static final String UCD_PHOT_COLOR_REDDFREE = "phot.color.reddFree"; //  Dereddened color
    public static final String UCD_PHOT_COUNT = "phot.count"; //  Flux expressed in counts
    public static final String UCD_PHOT_FLUENCE = "phot.fluence"; //  Radiant photon energy received by a surface per unit area or irradiance of a surface integrated over time of irradiation
    public static final String UCD_PHOT_FLUX = "phot.flux"; //  Photon flux or irradiance
    public static final String UCD_PHOT_FLUX_BOL = "phot.flux.bol"; //  Bolometric flux
    public static final String UCD_PHOT_FLUX_DENSITY = "phot.flux.density"; //  Flux density (per wl/freq/energy interval)
    public static final String UCD_PHOT_FLUX_DENSITY_SB = "phot.flux.density.sb"; //  Flux density surface brightness
    public static final String UCD_PHOT_FLUX_SB = "phot.flux.sb"; //  Flux surface brightness
    public static final String UCD_PHOT_LIMBDARK = "phot.limbDark"; //  Limb-darkening coefficients
    public static final String UCD_PHOT_MAG = "phot.mag"; //  Photometric magnitude
    public static final String UCD_PHOT_MAG_BC = "phot.mag.bc"; //  Bolometric correction
    public static final String UCD_PHOT_MAG_BOL = "phot.mag.bol"; //  Bolometric magnitude
    public static final String UCD_PHOT_MAG_DISTMOD = "phot.mag.distMod"; //  Distance modulus
    public static final String UCD_PHOT_MAG_REDDFREE = "phot.mag.reddFree"; //  Dereddened magnitude
    public static final String UCD_PHOT_MAG_SB = "phot.mag.sb"; //  Surface brightness in magnitude units
    public static final String UCD_PHOT_RADIANCE = "phot.radiance"; //  Radiance as energy flux per solid angle
    public static final String UCD_PHYS = "phys"; //  Physical quantities
    public static final String UCD_PHYS_SFR = "phys.SFR"; //  Star formation rate
    public static final String UCD_PHYS_ABSORPTION = "phys.absorption"; //  Extinction or absorption along the line of sight
    public static final String UCD_PHYS_ABSORPTION_COEFF = "phys.absorption.coeff"; //  Absorption coefficient (e.g. in a spectral line)
    public static final String UCD_PHYS_ABSORPTION_GAL = "phys.absorption.gal"; //  Galactic extinction
    public static final String UCD_PHYS_ABSORPTION_OPTICALDEPTH = "phys.absorption.opticalDepth"; //  Optical depth
    public static final String UCD_PHYS_ABUND = "phys.abund"; //  Abundance
    public static final String UCD_PHYS_ABUND_FE = "phys.abund.Fe"; //  Fe/H abundance
    public static final String UCD_PHYS_ABUND_X = "phys.abund.X"; //  Hydrogen abundance
    public static final String UCD_PHYS_ABUND_Y = "phys.abund.Y"; //  Helium abundance
    public static final String UCD_PHYS_ABUND_Z = "phys.abund.Z"; //  Metallicity abundance
    public static final String UCD_PHYS_ACCELERATION = "phys.acceleration"; //  Acceleration
    public static final String UCD_PHYS_AEROSOL = "phys.aerosol"; //  Relative to aerosol
    public static final String UCD_PHYS_ALBEDO = "phys.albedo"; //  Albedo or reflectance
    public static final String UCD_PHYS_ANGAREA = "phys.angArea"; //  Angular area
    public static final String UCD_PHYS_ANGMOMENTUM = "phys.angMomentum"; //  Angular momentum
    public static final String UCD_PHYS_ANGSIZE = "phys.angSize"; //  Angular size width diameter dimension extension major minor axis extraction radius
    public static final String UCD_PHYS_ANGSIZE_SMAJAXIS = "phys.angSize.smajAxis"; //  Angular size extent or extension of semi-major axis
    public static final String UCD_PHYS_ANGSIZE_SMINAXIS = "phys.angSize.sminAxis"; //  Angular size extent or extension of semi-minor axis
    public static final String UCD_PHYS_AREA = "phys.area"; //  Area (in surface, not angular units)
    public static final String UCD_PHYS_ATMOL = "phys.atmol"; //  Atomic and molecular physics (shared properties)
    public static final String UCD_PHYS_ATMOL_BRANCHINGRATIO = "phys.atmol.branchingRatio"; //  Branching ratio
    public static final String UCD_PHYS_ATMOL_COLLISIONAL = "phys.atmol.collisional"; //  Related to collisions
    public static final String UCD_PHYS_ATMOL_COLLSTRENGTH = "phys.atmol.collStrength"; //  Collisional strength
    public static final String UCD_PHYS_ATMOL_CONFIGURATION = "phys.atmol.configuration"; //  Configuration
    public static final String UCD_PHYS_ATMOL_CROSSSECTION = "phys.atmol.crossSection"; //  Atomic / molecular cross-section
    public static final String UCD_PHYS_ATMOL_ELEMENT = "phys.atmol.element"; //  Element
    public static final String UCD_PHYS_ATMOL_EXCITATION = "phys.atmol.excitation"; //  Atomic molecular excitation parameter
    public static final String UCD_PHYS_ATMOL_FINAL = "phys.atmol.final"; //  Quantity refers to atomic/molecular final/ground state, level, etc.
    public static final String UCD_PHYS_ATMOL_INITIAL = "phys.atmol.initial"; //  Quantity refers to atomic/molecular initial state, level, etc.
    public static final String UCD_PHYS_ATMOL_IONSTAGE = "phys.atmol.ionStage"; //  Ion, ionization stage
    public static final String UCD_PHYS_ATMOL_IONIZATION = "phys.atmol.ionization"; //  Related to ionization
    public static final String UCD_PHYS_ATMOL_LANDE = "phys.atmol.lande"; //  Lande factor
    public static final String UCD_PHYS_ATMOL_LEVEL = "phys.atmol.level"; //  Atomic level
    public static final String UCD_PHYS_ATMOL_LIFETIME = "phys.atmol.lifetime"; //  Lifetime of a level
    public static final String UCD_PHYS_ATMOL_LINESHIFT = "phys.atmol.lineShift"; //  Line shifting coefficient
    public static final String UCD_PHYS_ATMOL_NUMBER = "phys.atmol.number"; //  Atomic number Z
    public static final String UCD_PHYS_ATMOL_OSCSTRENGTH = "phys.atmol.oscStrength"; //  Oscillator strength
    public static final String UCD_PHYS_ATMOL_PARITY = "phys.atmol.parity"; //  Parity
    public static final String UCD_PHYS_ATMOL_QN = "phys.atmol.qn"; //  Quantum number
    public static final String UCD_PHYS_ATMOL_RADIATIONTYPE = "phys.atmol.radiationType"; //  Type of radiation characterizing atomic lines (electric dipole/quadrupole, magnetic dipole)
    public static final String UCD_PHYS_ATMOL_SYMMETRY = "phys.atmol.symmetry"; //  Type of nuclear spin symmetry
    public static final String UCD_PHYS_ATMOL_SWEIGHT = "phys.atmol.sWeight"; //  Statistical weight
    public static final String UCD_PHYS_ATMOL_SWEIGHT_NUCLEAR = "phys.atmol.sWeight.nuclear"; //  Statistical weight for nuclear spin states
    public static final String UCD_PHYS_ATMOL_TERM = "phys.atmol.term"; //  Atomic term
    public static final String UCD_PHYS_ATMOL_TRANSITION = "phys.atmol.transition"; //  Transition between states
    public static final String UCD_PHYS_ATMOL_TRANSPROB = "phys.atmol.transProb"; //  Transition probability, Einstein A coefficient
    public static final String UCD_PHYS_ATMOL_WOSCSTRENGTH = "phys.atmol.wOscStrength"; //  Weighted oscillator strength
    public static final String UCD_PHYS_ATMOL_WEIGHT = "phys.atmol.weight"; //  Atomic weight
    public static final String UCD_PHYS_COLUMNDENSITY = "phys.columnDensity"; //  Column density
    public static final String UCD_PHYS_COMPOSITION = "phys.composition"; //  Quantities related to composition of objects
    public static final String UCD_PHYS_COMPOSITION_MASSLIGHTRA = "phys.composition.massLightRatio"; //  Mass to light ratio
    public static final String UCD_PHYS_COMPOSITION_YIELD = "phys.composition.yield"; //  Mass yield
    public static final String UCD_PHYS_COSMOLOGY = "phys.cosmology"; //  Related to cosmology
    public static final String UCD_PHYS_DAMPING = "phys.damping"; //  Generic damping quantities
    public static final String UCD_PHYS_DENSITY = "phys.density"; //  Density (of mass, electron, ...)
    public static final String UCD_PHYS_DENSITY_PHASESPACE = "phys.density.phaseSpace"; //  Density in the phase space
    public static final String UCD_PHYS_DIELECTRIC = "phys.dielectric"; //  Complex dielectric function
    public static final String UCD_PHYS_DISPMEASURE = "phys.dispMeasure"; //  Dispersion measure
    public static final String UCD_PHYS_DUST = "phys.dust"; //  Relative to dust
    public static final String UCD_PHYS_ELECTFIELD = "phys.electField"; //  Electric field
    public static final String UCD_PHYS_ELECTRON = "phys.electron"; //  Electron
    public static final String UCD_PHYS_ELECTRON_DEGEN = "phys.electron.degen"; //  Electron degeneracy parameter
    public static final String UCD_PHYS_EMISSMEASURE = "phys.emissMeasure"; //  Emission measure
    public static final String UCD_PHYS_EMISSIVITY = "phys.emissivity"; //  Emissivity
    public static final String UCD_PHYS_ENERGY = "phys.energy"; //  Energy
    public static final String UCD_PHYS_ENERGY_GIBBS = "phys.energy.Gibbs"; //  Gibbs (free) energy or free enthalpy [G=H-TS]
    public static final String UCD_PHYS_ENERGY_HELMHOLTZ = "phys.energy.Helmholtz"; //  Helmholtz free energy [A=U-TS]
    public static final String UCD_PHYS_ENERGY_DENSITY = "phys.energy.density"; //  Energy density
    public static final String UCD_PHYS_ENTHALPY = "phys.enthalpy"; //  Enthalpy [H=U+pv]
    public static final String UCD_PHYS_ENTROPY = "phys.entropy"; //  Entropy
    public static final String UCD_PHYS_EOS = "phys.eos"; //  Equation of state
    public static final String UCD_PHYS_EXCITPARAM = "phys.excitParam"; //  Excitation parameter U
    public static final String UCD_PHYS_FLUENCE = "phys.fluence"; //  Particle energy received by a surface per unit area integrated over time
    public static final String UCD_PHYS_FLUX = "phys.flux"; //  Flux or flow of particle, energy, etc.
    public static final String UCD_PHYS_FLUX_ENERGY = "phys.flux.energy"; //  Energy flux, heat flux
    public static final String UCD_PHYS_GAUNTFACTOR = "phys.gauntFactor"; //  Gaunt factor/correction
    public static final String UCD_PHYS_GRAVITY = "phys.gravity"; //  Gravity
    public static final String UCD_PHYS_IONIZPARAM = "phys.ionizParam"; //  Ionization parameter
    public static final String UCD_PHYS_IONIZPARAM_COLL = "phys.ionizParam.coll"; //  Collisional ionization
    public static final String UCD_PHYS_IONIZPARAM_RAD = "phys.ionizParam.rad"; //  Radiative ionization
    public static final String UCD_PHYS_LUMINOSITY = "phys.luminosity"; //  Luminosity
    public static final String UCD_PHYS_LUMINOSITY_FUN = "phys.luminosity.fun"; //  Luminosity function
    public static final String UCD_PHYS_MAGABS = "phys.magAbs"; //  Absolute magnitude
    public static final String UCD_PHYS_MAGABS_BOL = "phys.magAbs.bol"; //  Bolometric absolute magnitude
    public static final String UCD_PHYS_MAGFIELD = "phys.magField"; //  Magnetic field
    public static final String UCD_PHYS_MASS = "phys.mass"; //  Mass
    public static final String UCD_PHYS_MASS_INERTIAMOMENTUM = "phys.mass.inertiaMomentum"; //  Momentum of inertia or rotational inertia
    public static final String UCD_PHYS_MASS_LOSS = "phys.mass.loss"; //  Mass loss
    public static final String UCD_PHYS_MOL = "phys.mol"; //  Molecular data
    public static final String UCD_PHYS_MOL_DIPOLE = "phys.mol.dipole"; //  Molecular dipole
    public static final String UCD_PHYS_MOL_DIPOLE_ELECTRIC = "phys.mol.dipole.electric"; //  Molecular electric dipole moment
    public static final String UCD_PHYS_MOL_DIPOLE_MAGNETIC = "phys.mol.dipole.magnetic"; //  Molecular magnetic dipole moment
    public static final String UCD_PHYS_MOL_DISSOCIATION = "phys.mol.dissociation"; //  Molecular dissociation
    public static final String UCD_PHYS_MOL_FORMATIONHEAT = "phys.mol.formationHeat"; //  Formation heat for molecules
    public static final String UCD_PHYS_MOL_QUADRUPOLE = "phys.mol.quadrupole"; //  Molecular quadrupole
    public static final String UCD_PHYS_MOL_QUADRUPOLE_ELECTRIC = "phys.mol.quadrupole.electric"; //  Molecular electric quadrupole moment
    public static final String UCD_PHYS_MOL_ROTATION = "phys.mol.rotation"; //  Molecular rotation
    public static final String UCD_PHYS_MOL_VIBRATION = "phys.mol.vibration"; //  Molecular vibration
    public static final String UCD_PHYS_PARTICLE = "phys.particle"; //  Related to physical particles
    public static final String UCD_PHYS_PARTICLE_NEUTRINO = "phys.particle.neutrino"; //  Related to neutrino
    public static final String UCD_PHYS_PARTICLE_NEUTRON = "phys.particle.neutron"; //  Related to neutron
    public static final String UCD_PHYS_PARTICLE_PROTON = "phys.particle.proton"; //  Related to proton
    public static final String UCD_PHYS_PARTICLE_ALPHA = "phys.particle.alpha"; //  Related to alpha particle
    public static final String UCD_PHYS_PHASESPACE = "phys.phaseSpace"; //  Related to phase space
    public static final String UCD_PHYS_POLARIZATION = "phys.polarization"; //  Polarization degree (or percentage)
    public static final String UCD_PHYS_POLARIZATION_CIRCULAR = "phys.polarization.circular"; //  Circular polarization
    public static final String UCD_PHYS_POLARIZATION_LINEAR = "phys.polarization.linear"; //  Linear polarization
    public static final String UCD_PHYS_POLARIZATION_ROTMEASURE = "phys.polarization.rotMeasure"; //  Rotation measure polarization
    public static final String UCD_PHYS_POLARIZATION_STOKES = "phys.polarization.stokes"; //  Stokes polarization
    public static final String UCD_PHYS_POLARIZATION_STOKES_I = "phys.polarization.stokes.I"; //  Stokes polarization coefficient I
    public static final String UCD_PHYS_POLARIZATION_STOKES_Q = "phys.polarization.stokes.Q"; //  Stokes polarization coefficient Q
    public static final String UCD_PHYS_POLARIZATION_STOKES_U = "phys.polarization.stokes.U"; //  Stokes polarization coefficient U
    public static final String UCD_PHYS_POLARIZATION_STOKES_V = "phys.polarization.stokes.V"; //  Stokes polarization coefficient V
    public static final String UCD_PHYS_POTENTIAL = "phys.potential"; //  Potential (electric, gravitational, etc)
    public static final String UCD_PHYS_PRESSURE = "phys.pressure"; //  Pressure
    public static final String UCD_PHYS_RECOMBINATION_COEFF = "phys.recombination.coeff"; //  Recombination coefficient
    public static final String UCD_PHYS_REFRACTINDEX = "phys.refractIndex"; //  Refraction index
    public static final String UCD_PHYS_SIZE = "phys.size"; //  Linear size, length (not angular)
    public static final String UCD_PHYS_SIZE_AXISRATIO = "phys.size.axisRatio"; //  Axis ratio (a/b) or (b/a)
    public static final String UCD_PHYS_SIZE_DIAMETER = "phys.size.diameter"; //  Diameter
    public static final String UCD_PHYS_SIZE_RADIUS = "phys.size.radius"; //  Radius
    public static final String UCD_PHYS_SIZE_SMAJAXIS = "phys.size.smajAxis"; //  Linear semi major axis
    public static final String UCD_PHYS_SIZE_SMINAXIS = "phys.size.sminAxis"; //  Linear semi minor axis
    public static final String UCD_PHYS_SIZE_SMEDAXIS = "phys.size.smedAxis"; //  Linear semi median axis for 3D ellipsoids
    public static final String UCD_PHYS_TEMPERATURE = "phys.temperature"; //  Temperature
    public static final String UCD_PHYS_TEMPERATURE_EFFECTIVE = "phys.temperature.effective"; //  Effective temperature
    public static final String UCD_PHYS_TEMPERATURE_ELECTRON = "phys.temperature.electron"; //  Electron temperature
    public static final String UCD_PHYS_TRANSMISSION = "phys.transmission"; //  Transmission (of filter, instrument, ...)
    public static final String UCD_PHYS_VELOC = "phys.veloc"; //  Space velocity
    public static final String UCD_PHYS_VELOC_ANG = "phys.veloc.ang"; //  Angular velocity
    public static final String UCD_PHYS_VELOC_DISPERSION = "phys.veloc.dispersion"; //  Velocity dispersion
    public static final String UCD_PHYS_VELOC_ESCAPE = "phys.veloc.escape"; //  Escape velocity
    public static final String UCD_PHYS_VELOC_EXPANSION = "phys.veloc.expansion"; //  Expansion velocity
    public static final String UCD_PHYS_VELOC_MICROTURB = "phys.veloc.microTurb"; //  Microturbulence velocity
    public static final String UCD_PHYS_VELOC_ORBITAL = "phys.veloc.orbital"; //  Orbital velocity
    public static final String UCD_PHYS_VELOC_PULSAT = "phys.veloc.pulsat"; //  Pulsational velocity
    public static final String UCD_PHYS_VELOC_ROTAT = "phys.veloc.rotat"; //  Rotational velocity
    public static final String UCD_PHYS_VELOC_TRANSVERSE = "phys.veloc.transverse"; //  Transverse / tangential velocity
    public static final String UCD_PHYS_VIRIAL = "phys.virial"; //  Related to virial quantities (mass, radius, ...)
    public static final String UCD_PHYS_VOLUME = "phys.volume"; //  Volume (in cubic units)
    public static final String UCD_POS = "pos"; //  Position and coordinates
    public static final String UCD_POS_ANGDISTANCE = "pos.angDistance"; //  Angular distance, elongation
    public static final String UCD_POS_ANGRESOLUTION = "pos.angResolution"; //  Angular resolution
    public static final String UCD_POS_AZ = "pos.az"; //  Position in alt-azimuth frame
    public static final String UCD_POS_AZ_ALT = "pos.az.alt"; //  Alt-azimuth altitude
    public static final String UCD_POS_AZ_AZI = "pos.az.azi"; //  Alt-azimuth azimuth
    public static final String UCD_POS_AZ_ZD = "pos.az.zd"; //  Alt-azimuth zenith distance
    public static final String UCD_POS_BARYCENTER = "pos.barycenter"; //  Barycenter
    public static final String UCD_POS_BODYRC = "pos.bodyrc"; //  Body related coordinates
    public static final String UCD_POS_BODYRC_ALT = "pos.bodyrc.alt"; //  Body related coordinate (altitude on the body)
    public static final String UCD_POS_BODYRC_LAT = "pos.bodyrc.lat"; //  Body related coordinate (latitude on the body)
    public static final String UCD_POS_BODYRC_LON = "pos.bodyrc.lon"; //  Body related coordinate (longitude on the body)
    public static final String UCD_POS_CARTESIAN = "pos.cartesian"; //  Cartesian (rectangular) coordinates
    public static final String UCD_POS_CARTESIAN_X = "pos.cartesian.x"; //  Cartesian coordinate along the x-axis
    public static final String UCD_POS_CARTESIAN_Y = "pos.cartesian.y"; //  Cartesian coordinate along the y-axis
    public static final String UCD_POS_CARTESIAN_Z = "pos.cartesian.z"; //  Cartesian coordinate along the z-axis
    public static final String UCD_POS_CENTROID = "pos.centroid"; //  Related to the centroid of a measure.
    public static final String UCD_POS_CMB = "pos.cmb"; //  Cosmic Microwave Background reference frame
    public static final String UCD_POS_DIRCOS = "pos.dirCos"; //  Direction cosine
    public static final String UCD_POS_DISTANCE = "pos.distance"; //  Linear distance
    public static final String UCD_POS_EARTH = "pos.earth"; //  Coordinates related to Earth
    public static final String UCD_POS_EARTH_ALTITUDE = "pos.earth.altitude"; //  Altitude, height on Earth  above sea level
    public static final String UCD_POS_EARTH_LAT = "pos.earth.lat"; //  Latitude on Earth
    public static final String UCD_POS_EARTH_LON = "pos.earth.lon"; //  Longitude on Earth
    public static final String UCD_POS_ECLIPTIC = "pos.ecliptic"; //  Ecliptic coordinates
    public static final String UCD_POS_ECLIPTIC_LAT = "pos.ecliptic.lat"; //  Ecliptic latitude
    public static final String UCD_POS_ECLIPTIC_LON = "pos.ecliptic.lon"; //  Ecliptic longitude
    public static final String UCD_POS_EOP = "pos.eop"; //  Earth orientation parameters
    public static final String UCD_POS_EPHEM = "pos.ephem"; //  Ephemeris
    public static final String UCD_POS_EQ = "pos.eq"; //  Equatorial coordinates
    public static final String UCD_POS_EQ_DEC = "pos.eq.dec"; //  Declination in equatorial coordinates
    public static final String UCD_POS_EQ_HA = "pos.eq.ha"; //  Hour-angle
    public static final String UCD_POS_EQ_RA = "pos.eq.ra"; //  Right ascension in equatorial coordinates
    public static final String UCD_POS_EQ_SPD = "pos.eq.spd"; //  South polar distance in equatorial coordinates
    public static final String UCD_POS_ERRORELLIPSE = "pos.errorEllipse"; //  Positional error ellipse
    public static final String UCD_POS_FRAME = "pos.frame"; //  Reference frame used for positions
    public static final String UCD_POS_GALACTIC = "pos.galactic"; //  Galactic coordinates
    public static final String UCD_POS_GALACTIC_LAT = "pos.galactic.lat"; //  Latitude in galactic coordinates
    public static final String UCD_POS_GALACTIC_LON = "pos.galactic.lon"; //  Longitude in galactic coordinates
    public static final String UCD_POS_GALACTOCENTRIC = "pos.galactocentric"; //  Galactocentric coordinate system
    public static final String UCD_POS_GEOCENTRIC = "pos.geocentric"; //  Geocentric coordinate system
    public static final String UCD_POS_HEALPIX = "pos.healpix"; //  Hierarchical Equal Area IsoLatitude Pixelization
    public static final String UCD_POS_HELIOCENTRIC = "pos.heliocentric"; //  Heliocentric position coordinate (solar system bodies)
    public static final String UCD_POS_HTM = "pos.HTM"; //  Hierarchical Triangular Mesh
    public static final String UCD_POS_LAMBERT = "pos.lambert"; //  Lambert projection
    public static final String UCD_POS_LG = "pos.lg"; //  Local Group reference frame
    public static final String UCD_POS_LSR = "pos.lsr"; //  Local Standard of Rest reference frame
    public static final String UCD_POS_LUNAR = "pos.lunar"; //  Lunar coordinates
    public static final String UCD_POS_LUNAR_OCCULT = "pos.lunar.occult"; //  Occultation by lunar limb
    public static final String UCD_POS_NUTATION = "pos.nutation"; //  Nutation (of a body)
    public static final String UCD_POS_OUTLINE = "pos.outline"; //  Set of points outlining a region (contour)
    public static final String UCD_POS_PARALLAX = "pos.parallax"; //  Parallax
    public static final String UCD_POS_PARALLAX_DYN = "pos.parallax.dyn"; //  Dynamical parallax
    public static final String UCD_POS_PARALLAX_PHOT = "pos.parallax.phot"; //  Photometric parallaxes
    public static final String UCD_POS_PARALLAX_SPECT = "pos.parallax.spect"; //  Spectroscopic parallax
    public static final String UCD_POS_PARALLAX_TRIG = "pos.parallax.trig"; //  Trigonometric parallax
    public static final String UCD_POS_PHASEANG = "pos.phaseAng"; //  Phase angle, e.g. elongation of earth from sun as seen from a third celestial object
    public static final String UCD_POS_PM = "pos.pm"; //  Proper motion
    public static final String UCD_POS_POSANG = "pos.posAng"; //  Position angle of a given vector
    public static final String UCD_POS_PRECESS = "pos.precess"; //  Precession (in equatorial coordinates)
    public static final String UCD_POS_SUPERGALACTIC = "pos.supergalactic"; //  Supergalactic coordinates
    public static final String UCD_POS_SUPERGALACTIC_LAT = "pos.supergalactic.lat"; //  Latitude in supergalactic coordinates
    public static final String UCD_POS_SUPERGALACTIC_LON = "pos.supergalactic.lon"; //  Longitude in supergalactic coordinates
    public static final String UCD_POS_WCS = "pos.wcs"; //  WCS keywords
    public static final String UCD_POS_WCS_CDMATRIX = "pos.wcs.cdmatrix"; //  WCS CDMATRIX
    public static final String UCD_POS_WCS_CRPIX = "pos.wcs.crpix"; //  WCS CRPIX
    public static final String UCD_POS_WCS_CRVAL = "pos.wcs.crval"; //  WCS CRVAL
    public static final String UCD_POS_WCS_CTYPE = "pos.wcs.ctype"; //  WCS CTYPE
    public static final String UCD_POS_WCS_NAXES = "pos.wcs.naxes"; //  WCS NAXES
    public static final String UCD_POS_WCS_NAXIS = "pos.wcs.naxis"; //  WCS NAXIS
    public static final String UCD_POS_WCS_SCALE = "pos.wcs.scale"; //  WCS scale or scale of an image
    public static final String UCD_SPECT = "spect"; //  Spectroscopy
    public static final String UCD_SPECT_BINSIZE = "spect.binSize"; //  Spectral bin size
    public static final String UCD_SPECT_CONTINUUM = "spect.continuum"; //  Continuum spectrum
    public static final String UCD_SPECT_DOPPLERPARAM = "spect.dopplerParam"; //  Doppler parameter b
    public static final String UCD_SPECT_DOPPLERVELOC = "spect.dopplerVeloc"; //  Radial velocity, derived from the shift of some spectral feature
    public static final String UCD_SPECT_DOPPLERVELOC_OPT = "spect.dopplerVeloc.opt"; //  Radial velocity derived from a wavelength shift using the optical convention
    public static final String UCD_SPECT_DOPPLERVELOC_RADIO = "spect.dopplerVeloc.radio"; //  Radial velocity derived from a frequency shift using the radio convention
    public static final String UCD_SPECT_INDEX = "spect.index"; //  Spectral index
    public static final String UCD_SPECT_LINE = "spect.line"; //  Spectral line
    public static final String UCD_SPECT_LINE_ASYMMETRY = "spect.line.asymmetry"; //  Line asymmetry
    public static final String UCD_SPECT_LINE_BROAD = "spect.line.broad"; //  Spectral line broadening
    public static final String UCD_SPECT_LINE_BROAD_STARK = "spect.line.broad.Stark"; //  Stark line broadening coefficient
    public static final String UCD_SPECT_LINE_BROAD_ZEEMAN = "spect.line.broad.Zeeman"; //  Zeeman broadening
    public static final String UCD_SPECT_LINE_EQWIDTH = "spect.line.eqWidth"; //  Line equivalent width
    public static final String UCD_SPECT_LINE_INTENSITY = "spect.line.intensity"; //  Line intensity
    public static final String UCD_SPECT_LINE_PROFILE = "spect.line.profile"; //  Line profile
    public static final String UCD_SPECT_LINE_STRENGTH = "spect.line.strength"; //  Spectral line strength S
    public static final String UCD_SPECT_LINE_WIDTH = "spect.line.width"; //  Spectral line full width half maximum
    public static final String UCD_SPECT_RESOLUTION = "spect.resolution"; //  Spectral (or velocity) resolution
    public static final String UCD_SRC = "src"; //  Observed source viewed on the sky
    public static final String UCD_SRC_CALIB = "src.calib"; //  Calibration source
    public static final String UCD_SRC_CALIB_GUIDESTAR = "src.calib.guideStar"; //  Guide star
    public static final String UCD_SRC_CLASS = "src.class"; //  Source classification (star, galaxy, cluster, comet, asteroid )
    public static final String UCD_SRC_CLASS_COLOR = "src.class.color"; //  Color classification
    public static final String UCD_SRC_CLASS_DISTANCE = "src.class.distance"; //  Distance class e.g. Abell
    public static final String UCD_SRC_CLASS_LUMINOSITY = "src.class.luminosity"; //  Luminosity class
    public static final String UCD_SRC_CLASS_RICHNESS = "src.class.richness"; //  Richness class e.g. Abell
    public static final String UCD_SRC_CLASS_STARGALAXY = "src.class.starGalaxy"; //  Star/galaxy discriminator, stellarity index
    public static final String UCD_SRC_CLASS_STRUCT = "src.class.struct"; //  Structure classification e.g. Bautz-Morgan
    public static final String UCD_SRC_DENSITY = "src.density"; //  Density of sources
    public static final String UCD_SRC_ELLIPTICITY = "src.ellipticity"; //  Source ellipticity
    public static final String UCD_SRC_IMPACTPARAM = "src.impactParam"; //  Impact parameter
    public static final String UCD_SRC_MORPH = "src.morph"; //  Morphology structure
    public static final String UCD_SRC_MORPH_PARAM = "src.morph.param"; //  Morphological parameter
    public static final String UCD_SRC_MORPH_SCLENGTH = "src.morph.scLength"; //  Scale length for a galactic component (disc or bulge)
    public static final String UCD_SRC_MORPH_TYPE = "src.morph.type"; //  Hubble morphological type (galaxies)
    public static final String UCD_SRC_NET = "src.net"; //  Qualifier indicating that a quantity (e.g. flux) is background subtracted rather than total
    public static final String UCD_SRC_ORBITAL = "src.orbital"; //  Orbital parameters
    public static final String UCD_SRC_ORBITAL_ECCENTRICITY = "src.orbital.eccentricity"; //  Orbit eccentricity
    public static final String UCD_SRC_ORBITAL_INCLINATION = "src.orbital.inclination"; //  Orbit inclination
    public static final String UCD_SRC_ORBITAL_MEANANOMALY = "src.orbital.meanAnomaly"; //  Orbit mean anomaly
    public static final String UCD_SRC_ORBITAL_MEANMOTION = "src.orbital.meanMotion"; //  Mean motion
    public static final String UCD_SRC_ORBITAL_NODE = "src.orbital.node"; //  Ascending node
    public static final String UCD_SRC_ORBITAL_PERIASTRON = "src.orbital.periastron"; //  Periastron
    public static final String UCD_SRC_ORBITAL_TISSERAND = "src.orbital.Tisserand"; //  Tisserand parameter (generic)
    public static final String UCD_SRC_ORBITAL_TISSJ = "src.orbital.TissJ"; //  Tisserand parameter with respect to Jupiter
    public static final String UCD_SRC_REDSHIFT = "src.redshift"; //  Redshift
    public static final String UCD_SRC_REDSHIFT_PHOT = "src.redshift.phot"; //  Photometric redshift
    public static final String UCD_SRC_SAMPLE = "src.sample"; //  Sample
    public static final String UCD_SRC_SPTYPE = "src.spType"; //  Spectral type MK
    public static final String UCD_SRC_VAR = "src.var"; //  Variability of source
    public static final String UCD_SRC_VAR_AMPLITUDE = "src.var.amplitude"; //  Amplitude of variation
    public static final String UCD_SRC_VAR_INDEX = "src.var.index"; //  Variability index
    public static final String UCD_SRC_VAR_PULSE = "src.var.pulse"; //  Pulse
    public static final String UCD_STAT = "stat"; //  Statistical parameters
    public static final String UCD_STAT_ASYMMETRY = "stat.asymmetry"; //  Measure of asymmetry
    public static final String UCD_STAT_CORRELATION = "stat.correlation"; //  Correlation between two parameters
    public static final String UCD_STAT_COVARIANCE = "stat.covariance"; //  Covariance between two parameters
    public static final String UCD_STAT_ERROR = "stat.error"; //  Statistical error
    public static final String UCD_STAT_ERROR_SYS = "stat.error.sys"; //  Systematic error
    public static final String UCD_STAT_FILLING = "stat.filling"; //  Filling factor (volume, time, ...)
    public static final String UCD_STAT_FIT = "stat.fit"; //  Fit
    public static final String UCD_STAT_FIT_CHI2 = "stat.fit.chi2"; //  Chi2
    public static final String UCD_STAT_FIT_DOF = "stat.fit.dof"; //  Degrees of freedom
    public static final String UCD_STAT_FIT_GOODNESS = "stat.fit.goodness"; //  Goodness or significance of fit
    public static final String UCD_STAT_FIT_OMC = "stat.fit.omc"; //  Observed minus computed
    public static final String UCD_STAT_FIT_PARAM = "stat.fit.param"; //  Parameter of fit
    public static final String UCD_STAT_FIT_RESIDUAL = "stat.fit.residual"; //  Residual fit
    public static final String UCD_STAT_FOURIER = "stat.Fourier"; //  Fourier coefficient
    public static final String UCD_STAT_FOURIER_AMPLITUDE = "stat.Fourier.amplitude"; //  Amplitude of Fourier coefficient
    public static final String UCD_STAT_FWHM = "stat.fwhm"; //  Full width at half maximum
    public static final String UCD_STAT_INTERVAL = "stat.interval"; //  Generic interval between two limits (defined as a pair of values)
    public static final String UCD_STAT_LIKELIHOOD = "stat.likelihood"; //  Likelihood
    public static final String UCD_STAT_MAX = "stat.max"; //  Maximum or upper limit
    public static final String UCD_STAT_MEAN = "stat.mean"; //  Mean, average value
    public static final String UCD_STAT_MEDIAN = "stat.median"; //  Median value
    public static final String UCD_STAT_MIN = "stat.min"; //  Minimum or lowest limit
    public static final String UCD_STAT_PARAM = "stat.param"; //  Parameter
    public static final String UCD_STAT_PROBABILITY = "stat.probability"; //  Probability
    public static final String UCD_STAT_RANK = "stat.rank"; //  Rank or order in list of sorted values
    public static final String UCD_STAT_RMS = "stat.rms"; //  Root mean square as square root of sum of squared values or quadratic mean
    public static final String UCD_STAT_SNR = "stat.snr"; //  Signal to noise ratio
    public static final String UCD_STAT_STDEV = "stat.stdev"; //  Standard deviation as the square root of the variance
    public static final String UCD_STAT_UNCALIB = "stat.uncalib"; //  Qualifier of a generic uncalibrated quantity
    public static final String UCD_STAT_VALUE = "stat.value"; //  Miscellaneous value
    public static final String UCD_STAT_VARIANCE = "stat.variance"; //  Variance
    public static final String UCD_STAT_WEIGHT = "stat.weight"; //  Statistical weight
    public static final String UCD_TIME = "time"; //  Time, generic quantity in units of time or date
    public static final String UCD_TIME_AGE = "time.age"; //  Age
    public static final String UCD_TIME_CREATION = "time.creation"; //  Creation time/date (of dataset, file, catalogue,...)
    public static final String UCD_TIME_CROSSING = "time.crossing"; //  Crossing time
    public static final String UCD_TIME_DURATION = "time.duration"; //  Interval of time describing the duration of a generic event or phenomenon
    public static final String UCD_TIME_END = "time.end"; //  End time/date of a generic event
    public static final String UCD_TIME_EPOCH = "time.epoch"; //  Instant of time related to a generic event (epoch, date, Julian date, time stamp/tag,...)
    public static final String UCD_TIME_EQUINOX = "time.equinox"; //  Equinox
    public static final String UCD_TIME_INTERVAL = "time.interval"; //  Time interval, time-bin, time elapsed between two events, not the duration of an event
    public static final String UCD_TIME_LIFETIME = "time.lifetime"; //  Lifetime
    public static final String UCD_TIME_PERIOD = "time.period"; //  Period, interval of time between the recurrence of phases in a periodic phenomenon
    public static final String UCD_TIME_PERIOD_REVOLUTION = "time.period.revolution"; //  Period of revolution of a body around a primary one (similar to year)
    public static final String UCD_TIME_PERIOD_ROTATION = "time.period.rotation"; //  Period of rotation of a body around its axis (similar to day)
    public static final String UCD_TIME_PHASE = "time.phase"; //  Phase, position within a period
    public static final String UCD_TIME_PROCESSING = "time.processing"; //  A time/date associated with the processing of data
    public static final String UCD_TIME_PUBLIYEAR = "time.publiYear"; //  Publication year
    public static final String UCD_TIME_RELAX = "time.relax"; //  Relaxation time
    public static final String UCD_TIME_RELEASE = "time.release"; //  The time/date data is available to the public
    public static final String UCD_TIME_RESOLUTION = "time.resolution"; //  Time resolution
    public static final String UCD_TIME_SCALE = "time.scale"; //  Timescale
    public static final String UCD_TIME_START = "time.start"; //  Start time/date of generic event

    /*************************************************************************/
    /** TREE MAP **/
    /*************************************************************************/
    
    public static final String TREEMAP_TYPE_MISSION = "mission";
    public static final String TREEMAP_TYPE_SERVICE = "service";
    public static final String TREEMAP_TYPE_SUBCOLLECTION= "collection";
    public static final String TREEMAP_TYPE_DATAPRODUCT = "dataproduct";

    public static final int TREEMAP_LEVEL_SERVICE = 0;
    public static final int TREEMAP_LEVEL_1 = 1;
    public static final int TREEMAP_LEVEL_2 = 2;
    public static final int TREEMAP_LEVEL_3 = 3;
    
    /*************************************************************************/
    /** MOC **/
    /*************************************************************************/
    
    public static final String HEALPIX_IPIX = "healpix_index";
    public static final String HEALPIX_ORDER = "healpix_order";
    public static final String HEALPIX_COUNT = "healpix_count";
    
    public static final String Q3C_IPIX = "moc_ipix";
    public static final String Q3C_ORDER = "moc_order";
    public static final String Q3C_COUNT = "moc_count";
    
    /*************************************************************************/
    /** JWST Instruments **/
    /*************************************************************************/
    
    public enum JWSTInstrument {
        FGS1("FGS", "FGS1_FULL"),
        FGS2("FGS", "FGS2_FULL"), 
        NIRISS_CEN("NIRISS","NIS_CEN"),
        NIRSPEC_MSA("NIRSpec","NRS_FULL_MSA"),
        NIRSPEC_MSA1("NIRSpec","NRS_FULL_MSA1"),
        NIRSPEC_MSA2("NIRSpec","NRS_FULL_MSA2"),
        NIRSPEC_MSA3("NIRSpec","NRS_FULL_MSA3"),
        NIRSPEC_MSA4("NIRSpec","NRS_FULL_MSA4"),
        NIRSPEC_IFU("NIRSpec","NRS_FULL_IFU"),
        NIRSPEC_SLIT1("NIRSpec","NRS_S200A1_SLIT"),
        NIRSPEC_SLIT2("NIRSpec","NRS_S200A2_SLIT"),
        NIRSPEC_SLIT3("NIRSpec","NRS_S200B1_SLIT"),
        NIRSPEC_SLIT4("NIRSpec","NRS_S400A1_SLIT"),
        NIRSPEC_SLIT5("NIRSpec","NRS_S1600A1_SLIT"),
        NIRCAFULL("NIRCam","NRCALL_FULL"),
        NIRCA1("NIRCam","NRCA1_FULL"),
        NIRCA2("NIRCam","NRCA2_FULL"),
        NIRCA3("NIRCam","NRCA3_FULL"),
        NIRCA4("NIRCam","NRCA4_FULL"),
        NIRCA5("NIRCam","NRCA5_FULL_OSS"),
        NIRCB1("NIRCam","NRCB1_FULL"),
        NIRCB2("NIRCam","NRCB2_FULL"),
        NIRCB3("NIRCam","NRCB3_FULL"),
        NIRCB4("NIRCam","NRCB4_FULL"),
        NIRCB5("NIRCam","NIRCB5_FULL"),
        NIRCA2_MASK210R("NIRCam","NRCA2_MASK210R"),
        NIRCA5_MASK335R("NIRCam","NRCA5_MASK335R"),
        NIRCA5_MASK430R("NIRCam","NRCA5_MASK430R"),
        NIRCA4_MASKSWB("NIRCam","NRCA4_MASKSWB"),
        NIRCA5_MASKLWB("NIRCam","NRCA5_MASKLWB"),
        NIRCB1_MASK210R("NIRCam","NRCB1_MASK210R"),
        NIRCB5_MASK335R("NIRCam","NRCB5_MASK335R"),
        NIRCB5_MASK430R("NIRCam","NRCB5_MASK430R"),
        NIRCB3_MASKSWB("NIRCam","NRCB3_MASKSWB"),
        NIRCB5_MASKLWB("NIRCam","NRCB5_MASKLWB"),
        MIRIM_FULL("MIRI","MIRIM_FULL"),     
        MIRIM_MASK1065("MIRI","MIRIM_MASK1065"),
        MIRIM_ILLUM("MIRI","MIRIM_ILLUM"),
        MIRIM_FP1MIMF("MIRI","MIRIM_FP1MIMF"),
        MIRIM_MASK1140("MIRI","MIRIM_MASK1140"),
        MIRIM_MASK1550("MIRI","MIRIM_MASK1550"),
        MIRIM_MASKLYOT("MIRI","MIRIM_MASKLYOT"),
        MIRIM_CHANNEL1A("MIRI","MIRIFU_CHANNEL1A");

    	private String instrName;
    	private String aperName;
    	private JWSTInstrument(String instrName, String aperName) {
    		this.instrName = instrName;
    		this.aperName = aperName;
    	}
		@Override
		public String toString() {
			return instrName;
		}
		
		public String getAperName() {
			return aperName;
		}
        
        
    }
    
    /*************************************************************************/
    /** DEFAULT INIT VALUES **/
    /*************************************************************************/
    public enum ReturnType {
        JSON("json", "application/json"), VOTABLE("vot", "application/x-votable+xml"), CSV("csv", "text/csv"), ASCII("ascii", "");

        private String name;
        private String mimeType;
        private ReturnType(String name, String mimeType) {
            this.name = name;
            this.mimeType = mimeType;
        }
        @Override
        public String toString() {
            return name;
        }
        
        public String getMimeType() {
            return mimeType;
        }
        
        
    }

    /**
     * HTTP GET actions enum.
     * @author mhsarmiento
     *
     */
    public enum HttpServlet {
        DATA_GET_SERVLET("/servlet/data-action?"), METADATA_GET_SERVLET("/servlet/metadata-action?"), METADATA_COUNT_SERVLET(
                "/servlet/metadata-count-action?"), SUGGESTION_SERVLET(
                        "/servlet/suggestion-action?"), RSS_GET_SERVLET("/servlet/rss-action?"), TARGET_NAME_RESOLVER(
                                "/servlet/targetresolver-action?"), FITS_SPECTRA_SERVLET(
                "/servlet/fits-spectra-action?"), FITS_METADATA_SERVLET(
                "/servlet/fits-metadata-action?"), FITS_IMAGE_SERVLET("/servlet/fits-image-action?"), QUERY_RESULTS_DOWNLOAD_SERVLET(
                                                        "/servlet/query-results-download-action?"), TINY_URL_SERVLET(
                                                                "/servlet/tiny-url-action?"), VOTABLE_BUFFER_SERVLET("/servlet/votable-buffer?");

        /** enum attrubite. */
        private String value;

        /**
         * classConstrutor.
         * @param inputValue Input String.
         */
        HttpServlet(final String inputValue) {
            this.value = inputValue;
        }

        @Override
        public String toString() {
            return value;
        }

        /**
         * getValue().
         * @return String
         */
        public String getValue() {
            return value;
        }

    }
    
    /** Prevents Utility class calls. */
    protected EsaSkyConstants() {
        // prevents calls from subclass
        throw new UnsupportedOperationException();
    }
    
    public static List<SimpleEntry<String, String>> getAvailableLanguages(){
        return AVAILABLE_LANGUAGES;
    }
}
