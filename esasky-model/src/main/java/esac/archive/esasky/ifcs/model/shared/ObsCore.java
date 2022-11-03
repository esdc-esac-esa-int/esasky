package esac.archive.esasky.ifcs.model.shared;

public enum ObsCore implements IContentDescriptor {
    DATAPRODUCT_TYPE("ObsDataset.dataProductType"), // Data product (file content) primary type
    DATAPRODUCT_SUBTYPE("ObsDataset.dataProductSubtype"), // Data product specific type
    CALIB_LEVEL("ObsDataset.calibLevel"), // Calibration level of the observation: in {0, 1, 2, 3, 4}
    TARGET_NAME("Target.name"), // Object of interest
    TARGET_CLASS("Target.class"), // Class of the Target object as in SSA
    OBS_ID("DataID.observationID"), // Internal  ID given by the ObsTAP service
    OBS_TITLE("DataID.title"), // Brief description of dataset in free format
    OBS_COLLECTION("DataID.collection"), // Name of the data collection
    OBS_CREATION_DATE("DataID.date"), // Date when the dataset was created
    OBS_CREATOR_NAME("DataID.creator"), // Name of the creator of the data
    OBS_CREATOR_DID("DataID.creatorDID"), // IVOA dataset identifier given by the creator
    OBS_RELEASE_DATE("Curation.releaseDate"), // Observation release date (ISO 8601)
    OBS_PUBLISHER_DID("Curation.publisherDID"), // ID for the Dataset   given by the publisher.
    PUBLISHER_ID("Curation.publisherID"), // IVOA-ID for the Publisher
    BIB_REFERENCE("Curation.reference"), // Service bibliographic reference
    DATA_RIGHTS("Curation.rights"), // Public/Secure/Proprietary/
    ACCESS_URL("Access.reference"), // URL used to access dataset
    ACCESS_FORMAT("Access.format"), // Content format of the dataset
    ACCESS_ESTSIZE("Access.size"), // Estimated size of dataset: in kilobytes
    S_RA("Char.SpatialAxis.Coverage.Location.Coord.Position2D.Value2.C1"), // Central Spatial Position in ICRS Right ascension
    S_DEC("Char.SpatialAxis.Coverage.Location.Coord.Position2D.Value2.C2"), // Central Spatial Position in ICRS Declination
    S_FOV("Char.SpatialAxis.Coverage.Bounds.Extent.diameter"), // Estimated size of the covered region as the diameter of a containing circle
    S_REGION("Char.SpatialAxis.Coverage.Support.Area"), // Sky region covered by the  data product (expressed in ICRS frame)
    S_RESOLUTION("Char.SpatialAxis.Resolution.refval.value"), // Spatial resolution of data as FWHM of PSF
    S_XEL1("Char.SpatialAxis.numBins1"), // Number of elements along the first coordinate of the spatial  axis
    S_XEL2("Char.SpatialAxis.numBins2"), // Number of elements along the second coordinate of the spatial  axis
    S_UCD("Char.SpatialAxis.ucd"), // UCD for the nature of the spatial axis (pos or u,v data)
    S_UNIT("Char.SpatialAxis.unit"), // Unit used for spatial axis
    S_RESOLUTION_MIN("Char.SpatialAxis.Resolution.Bounds.Limits.LoLimit"), // Resolution min value on spatial axis (FHWM of PSF)
    S_RESOLUTION_MAX("Char.SpatialAxis .Resolution.Bounds. Limits.HiLimit"), // Resolution max value on spatial axis
    S_CALIB_STATUS("Char.SpatialAxis.calibrationStatus"), // Type of calibration along the spatial axis
    S_STAT_ERROR("Char.SpatialAxis.Accuracy.StatError.Refval.value"), // Astrometric precision along  the spatial axis
    S_PIXEL_SCALE("Char.SpatialAxis.Sampling.RefVal.SamplingPeriod"), // Sampling period in world coordinate units along the spatial axis
    T_XEL("Char.TimeAxis.numBins"), // Number of elements along the time axis
    T_REFPOS("Char.TimeAxis.ReferencePosition "), // Time Axis Reference Position as defined in STC REC, Section 4.4.1.1.1
    T_MIN("Char.TimeAxis.Coverage.Bounds.Limits.StartTime"), // Start time in MJD
    T_MAX("Char.TimeAxis.Coverage.Bounds.Limits.StopTime"), // Stop time  in MJD
    T_EXPTIME("Char.TimeAxis.Coverage.Support.Extent"), // Total exposure time
    T_RESOLUTION("Char.TimeAxis.Resolution.Refval.valueResolution.Refval.value"), // Temporal resolution FWHM
    T_CALIB_STATUS("Char.TimeAxis. calibrationStatus"), // Type of time coordinate calibration
    T_STAT_ERROR("Char.TimeAxis.Accuracy.StatError.Refval.value"), // Time coord statistical error
    EM_XEL("Char.SpectralAxis. numBins"), // Number of elements along the spectral axis
    EM_UCD("Char.SpectralAxis.ucd"), // Nature of the spectral axis
    EM_UNIT("Char.SpectralAxis.unit"), // Units along  the spectral axis
    EM_CALIB_STATUS("Char.SpectralAxis. calibrationStatus"), // Type of spectral coord calibration
    EM_MIN("Char.SpectralAxis.Coverage.Bounds.Limits.LoLimit"), // start in spectral coordinates
    EM_MAX("Char.SpectralAxis.Coverage.Bounds.Limits.HiLimit"), // stop in spectral coordinates
    EM_RES_POWER("Char.SpectralAxis.Resolution.ResolPower.refVal"), // Value of the resolving power along the spectral axis. (R)
    EM_RES_POWER_MIN("Char.SpectralAxis.Resolution.ResolPower.LoLimit"), // Resolving power  min value on spectral axis
    EM_RES_POWER_MAX("Char.SpectralAxis.Resolution.ResolPower.HiLimit"), // Resolving power max value on spectral axis
    EM_RESOLUTION("Char.SpectralAxis.Resolution.Refval.value"), // Value of Resolution along the spectral axis
    EM_STAT_ERROR("Char.SpectralAxis.Accuracy.StatError.Refval.value"), // Spectral coord statistical error
    O_UCD("Char.ObservableAxis.ucd"), // Nature of the observable axis
    O_UNIT("Char.ObservableAxis.unit"), // Units used for the observable values
    O_CALIB_STATUS("Char.ObservableAxis.calibrationStatus"), // Type of calibration for the observable coordinate
    O_STAT_ERROR("Char.ObservableAxis.Accuracy.StatError.Refval.value"), // Statistical error on the Observable axis
    POL_XEL("Char.PolarizationAxis.numBins "), // Number of elements along the polarization axis
    POL_STATES("Char.PolarizationAxis.stateList"), // List of polarization states present in the data file
    INSTRUMENT_NAME("Provenance.ObsConfig.Instrument.name"), // The name of the instrument used for the observation
    PROPOSAL_ID("Provenance.Proposal.identifier"); // Identifier of proposal to which observation belongs
    private final String value;

    ObsCore(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public boolean matches(String str) {
        return str.toLowerCase().contains(value.toLowerCase());
    }

    @Override
    public String toString() {
        return value;
    }
}
