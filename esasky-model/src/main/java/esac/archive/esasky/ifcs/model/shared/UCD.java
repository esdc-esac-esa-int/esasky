package esac.archive.esasky.ifcs.model.shared;

public enum UCD implements IContentDescriptor{
    ARITH("arith"), //  Arithmetic quantities
    ARITH_DIFF("arith.diff"), //  Difference between two quantities described by the same UCD
    ARITH_FACTOR("arith.factor"), //  Numerical factor
    ARITH_GRAD("arith.grad"), //  Gradient
    ARITH_RATE("arith.rate"), //  Rate (per time unit)
    ARITH_RATIO("arith.ratio"), //  Ratio between two quantities described by the same UCD
    ARITH_SQUARED("arith.squared"), //  Squared quantity
    ARITH_SUM("arith.sum"), //  Summed or integrated quantity
    ARITH_VARIATION("arith.variation"), //  Generic variation of a quantity
    ARITH_ZP("arith.zp"), //  Zero point
    EM("em"), //  Electromagnetic spectrum
    EM_IR("em.IR"), //  Infrared part of the spectrum
    EM_IR_J("em.IR.J"), //  Infrared between 1.0 and 1.5 micron
    EM_IR_H("em.IR.H"), //  Infrared between 1.5 and 2 micron
    EM_IR_K("em.IR.K"), //  Infrared between 2 and 3 micron
    EM_IR_3_4UM("em.IR.3-4um"), //  Infrared between 3 and 4 micron
    EM_IR_4_8UM("em.IR.4-8um"), //  Infrared between 4 and 8 micron
    EM_IR_8_15UM("em.IR.8-15um"), //  Infrared between 8 and 15 micron
    EM_IR_15_30UM("em.IR.15-30um"), //  Infrared between 15 and 30 micron
    EM_IR_30_60UM("em.IR.30-60um"), //  Infrared between 30 and 60 micron
    EM_IR_60_100UM("em.IR.60-100um"), //  Infrared between 60 and 100 micron
    EM_IR_NIR("em.IR.NIR"), //  Near-Infrared), 1-5 microns
    EM_IR_MIR("em.IR.MIR"), //  Medium-Infrared), 5-30 microns
    EM_IR_FIR("em.IR.FIR"), //  Far-Infrared), 30-100 microns
    EM_UV("em.UV"), //  Ultraviolet part of the spectrum
    EM_UV_10_50NM("em.UV.10-50nm"), //  Ultraviolet between 10 and 50 nm EUV extreme UV
    EM_UV_50_100NM("em.UV.50-100nm"), //  Ultraviolet between 50 and 100 nm
    EM_UV_100_200NM("em.UV.100-200nm"), //  Ultraviolet between 100 and 200 nm FUV Far UV
    EM_UV_200_300NM("em.UV.200-300nm"), //  Ultraviolet between 200 and 300 nm NUV near UV
    EM_X_RAY("em.X-ray"), //  X-ray part of the spectrum
    EM_X_RAY_SOFT("em.X-ray.soft"), //  Soft X-ray (0.12 - 2 keV)
    EM_X_RAY_MEDIUM("em.X-ray.medium"), //  Medium X-ray (2 - 12 keV)
    EM_X_RAY_HARD("em.X-ray.hard"), //  Hard X-ray (12 - 120 keV)
    EM_BIN("em.bin"), //  Channel / instrumental spectral bin coordinate (bin number)
    EM_ENERGY("em.energy"), //  Energy value in the em frame
    EM_FREQ("em.freq"), //  Frequency value in the em frame
    EM_FREQ_CUTOFF("em.freq.cutoff"), //  cutoff frequency
    EM_FREQ_RESONANCE("em.freq.resonance"), //  resonance frequency
    EM_GAMMA("em.gamma"), //  Gamma rays part of the spectrum
    EM_GAMMA_SOFT("em.gamma.soft"), //  Soft gamma ray (120 - 500 keV)
    EM_GAMMA_HARD("em.gamma.hard"), //  Hard gamma ray (>500 keV)
    EM_LINE("em.line"), //  Designation of major atomic lines
    EM_LINE_HI("em.line.HI"), //  21cm hydrogen line
    EM_LINE_LYALPHA("em.line.Lyalpha"), //  H-Lyalpha line
    EM_LINE_HALPHA("em.line.Halpha"), //  H-alpha line
    EM_LINE_HBETA("em.line.Hbeta"), //  H-beta line
    EM_LINE_HGAMMA("em.line.Hgamma"), //  H-gamma line
    EM_LINE_HDELTA("em.line.Hdelta"), //  H-delta line
    EM_LINE_BRGAMMA("em.line.Brgamma"), //  Bracket gamma line
    EM_LINE_CO("em.line.CO"), //  CO radio line), e.g. 12CO(1-0) at 115GHz
    EM_LINE_OIII("em.line.OIII"), //  [OIII] line whose rest wl is 500.7 nm
    EM_MM("em.mm"), //  Millimetric/submillimetric part of the spectrum
    EM_MM_30_50GHZ("em.mm.30-50GHz"), //  Millimetric between 30 and 50 GHz
    EM_MM_50_100GHZ("em.mm.50-100GHz"), //  Millimetric between 50 and 100 GHz
    EM_MM_100_200GHZ("em.mm.100-200GHz"), //  Millimetric between 100 and 200 GHz
    EM_MM_200_400GHZ("em.mm.200-400GHz"), //  Millimetric between 200 and 400 GHz
    EM_MM_400_750GHZ("em.mm.400-750GHz"), //  Millimetric between 400 and 750 GHz
    EM_MM_750_1500GHZ("em.mm.750-1500GHz"), //  Millimetric between 750 and 1500 GHz
    EM_MM_1500_3000GHZ("em.mm.1500-3000GHz"), //  Millimetric between 1500 and 3000 GHz
    EM_OPT("em.opt"), //  Optical part of the spectrum
    EM_OPT_U("em.opt.U"), //  Optical band between 300 and 400 nm
    EM_OPT_B("em.opt.B"), //  Optical band between 400 and 500 nm
    EM_OPT_V("em.opt.V"), //  Optical band between 500 and 600 nm
    EM_OPT_R("em.opt.R"), //  Optical band between 600 and 750 nm
    EM_OPT_I("em.opt.I"), //  Optical band between 750 and 1000 nm
    EM_PW("em.pw"), //  Plasma waves (trapped in local medium)
    EM_RADIO("em.radio"), //  Radio part of the spectrum
    EM_RADIO_20MHZ("em.radio.20MHz"), //  Radio below 20 MHz
    EM_RADIO_20_100MHZ("em.radio.20-100MHz"), //  Radio between 20 and 100 MHz
    EM_RADIO_100_200MHZ("em.radio.100-200MHz"), //  Radio between 100 and 200 MHz
    EM_RADIO_200_400MHZ("em.radio.200-400MHz"), //  Radio between 200 and 400 MHz
    EM_RADIO_400_750MHZ("em.radio.400-750MHz"), //  Radio between 400 and 750 MHz
    EM_RADIO_750_1500MHZ("em.radio.750-1500MHz"), //  Radio between 750 and 1500 MHz
    EM_RADIO_1500_3000MHZ("em.radio.1500-3000MHz"), //  Radio between 1500 and 3000 MHz
    EM_RADIO_3_6GHZ("em.radio.3-6GHz"), //  Radio between 3 and 6 GHz
    EM_RADIO_6_12GHZ("em.radio.6-12GHz"), //  Radio between 6 and 12 GHz
    EM_RADIO_12_30GHZ("em.radio.12-30GHz"), //  Radio between 12 and 30 GHz
    EM_WAVENUMBER("em.wavenumber"), //  Wavenumber value in the em frame
    EM_WL("em.wl"), //  Wavelength value in the em frame
    EM_WL_CENTRAL("em.wl.central"), //  Central wavelength
    EM_WL_EFFECTIVE("em.wl.effective"), //  Effective wavelength
    INSTR("instr"), //  Instrument
    INSTR_BACKGROUND("instr.background"), //  Instrumental background
    INSTR_BANDPASS("instr.bandpass"), //  Bandpass (e.g.: band name) of instrument
    INSTR_BANDWIDTH("instr.bandwidth"), //  Bandwidth of the instrument
    INSTR_BASELINE("instr.baseline"), //  Baseline for interferometry
    INSTR_BEAM("instr.beam"), //  Beam
    INSTR_CALIB("instr.calib"), //  Calibration parameter
    INSTR_DET("instr.det"), //  Detector
    INSTR_DET_NOISE("instr.det.noise"), //  Instrument noise
    INSTR_DET_PSF("instr.det.psf"), //  Point Spread Function
    INSTR_DET_QE("instr.det.qe"), //  Quantum efficiency
    INSTR_DISPERSION("instr.dispersion"), //  Dispersion of a spectrograph
    INSTR_EXPERIMENT("instr.experiment"), //  Experiment or group of instruments
    INSTR_FILTER("instr.filter"), //  Filter
    INSTR_FOV("instr.fov"), //  Field of view
    INSTR_OBSTY("instr.obsty"), //  Observatory), satellite), mission
    INSTR_OBSTY_SEEING("instr.obsty.seeing"), //  Seeing
    INSTR_OFFSET("instr.offset"), //  Offset angle respect to main direction of observation
    INSTR_ORDER("instr.order"), //  Spectral order in a spectrograph
    INSTR_PARAM("instr.param"), //  Various instrumental parameters
    INSTR_PIXEL("instr.pixel"), //  Pixel (default size: angular)
    INSTR_PLATE("instr.plate"), //  Photographic plate
    INSTR_PLATE_EMULSION("instr.plate.emulsion"), //  Plate emulsion
    INSTR_PRECISION("instr.precision"), //  Instrument precision
    INSTR_RMSF("instr.rmsf"), //  Rotation Measure Spread Function
    INSTR_SATURATION("instr.saturation"), //  Instrument saturation threshold
    INSTR_SCALE("instr.scale"), //  Instrument scale (for CCD), plate), image)
    INSTR_SENSITIVITY("instr.sensitivity"), //  Instrument sensitivity), detection threshold
    INSTR_SETUP("instr.setup"), //  Instrument configuration or setup
    INSTR_SKYLEVEL("instr.skyLevel"), //  Sky level
    INSTR_SKYTEMP("instr.skyTemp"), //  Sky temperature
    INSTR_TEL("instr.tel"), //  Telescope
    INSTR_TEL_FOCALLENGTH("instr.tel.focalLength"), //  Telescope focal length
    INSTR_VOXEL("instr.voxel"), //  Related to a voxel (n-D volume element with n>2)
    META("meta"), //  Metadata
    META_ABSTRACT("meta.abstract"), //  Abstract (of paper), proposal), etc.)
    META_BIB("meta.bib"), //  Bibliographic reference
    META_BIB_AUTHOR("meta.bib.author"), //  Author name
    META_BIB_BIBCODE("meta.bib.bibcode"), //  Bibcode
    META_BIB_FIG("meta.bib.fig"), //  Figure in a paper
    META_BIB_JOURNAL("meta.bib.journal"), //  Journal name
    META_BIB_PAGE("meta.bib.page"), //  Page number
    META_BIB_VOLUME("meta.bib.volume"), //  Volume number
    META_CALIBLEVEL("meta.calibLevel"), //  Processing/calibration level
    META_CODE("meta.code"), //  Code or flag
    META_CODE_CLASS("meta.code.class"), //  Classification code
    META_CODE_ERROR("meta.code.error"), //  Limit uncertainty error flag
    META_CODE_MEMBER("meta.code.member"), //  Membership code
    META_CODE_MIME("meta.code.mime"), //  MIME type
    META_CODE_MULTIP("meta.code.multip"), //  Multiplicity or binarity flag
    META_CODE_QUAL("meta.code.qual"), //  Quality), precision), reliability flag or code
    META_CODE_STATUS("meta.code.status"), //  Status code (e.g.: status of a proposal/observation)
    META_CRYPTIC("meta.cryptic"), //  Unknown or impossible to understand quantity
    META_CURATION("meta.curation"), //  Identity of man/organization responsible for the data
    META_DATASET("meta.dataset"), //  Dataset
    META_EMAIL("meta.email"), //  Curation/contact e-mail
    META_FILE("meta.file"), //  File
    META_FITS("meta.fits"), //  FITS standard
    META_ID("meta.id"), //  Identifier), name or designation
    META_ID_ASSOC("meta.id.assoc"), //  Identifier of associated counterpart
    META_ID_COI("meta.id.CoI"), //  Name of Co-Investigator
    META_ID_CROSS("meta.id.cross"), //  Cross identification
    META_ID_PARENT("meta.id.parent"), //  Identification of parent source
    META_ID_PART("meta.id.part"), //  Part of identifier), suffix or sub-component
    META_ID_PI("meta.id.PI"), //  Name of Principal Investigator or Co-PI
    META_MAIN("meta.main"), //  Main value of something
    META_MODELLED("meta.modelled"), //  Quantity was produced by a model
    META_NOTE("meta.note"), //  Note or remark (longer than a code or flag)
    META_NUMBER("meta.number"), //  Number (of things), e.g. nb of object in an image)
    META_RECORD("meta.record"), //  Record number
    META_PREVIEW("meta.preview"), //  Related to a preview operation for a dataset
    META_QUERY("meta.query"), //  A query posed to an information system or database or a property of it
    META_REF("meta.ref"), //  Reference or origin
    META_REF_DOI("meta.ref.doi"), //  DOI identifier (dereferenceable)
    META_REF_IVOID("meta.ref.ivoid"), //  Related to an identifier as recommended in the IVOA (dereferenceable)
    META_REF_URI("meta.ref.uri"), //  URI), universal resource identifier
    META_REF_URL("meta.ref.url"), //  URL), web address
    META_SOFTWARE("meta.software"), //  Software used in generating data
    META_TABLE("meta.table"), //  Table or catalogue
    META_TITLE("meta.title"), //  Title or explanation
    META_UCD("meta.ucd"), //  UCD
    META_UNIT("meta.unit"), //  Unit
    META_VERSION("meta.version"), //  Version
    OBS("obs"), //  Observation
    OBS_AIRMASS("obs.airMass"), //  Airmass
    OBS_ATMOS("obs.atmos"), //  Atmosphere), atmospheric phenomena affecting an observation
    OBS_ATMOS_EXTINCTION("obs.atmos.extinction"), //  Atmospheric extinction
    OBS_ATMOS_REFRACTANGLE("obs.atmos.refractAngle"), //  Atmospheric refraction angle
    OBS_CALIB("obs.calib"), //  Calibration observation
    OBS_CALIB_FLAT("obs.calib.flat"), //  Related to flat-field calibration observation (dome), sky), ..)
    OBS_CALIB_DARK("obs.calib.dark"), //  Related to dark current calibration
    OBS_EXPOSURE("obs.exposure"), //  Exposure
    OBS_FIELD("obs.field"), //  Region covered by the observation
    OBS_IMAGE("obs.image"), //  Image
    OBS_OBSERVER("obs.observer"), //  Observer), discoverer
    OBS_OCCULT("obs.occult"), //  Observation of occultation phenomenon by solar system objects
    OBS_TRANSIT("obs.transit"), //  Observation of transit phenomenon  : exo-planets
    OBS_PARAM("obs.param"), //  Various observation or reduction parameter
    OBS_PROPOSAL("obs.proposal"), //  Observation proposal
    OBS_PROPOSAL_CYCLE("obs.proposal.cycle"), //  Proposal cycle
    OBS_SEQUENCE("obs.sequence"), //  Sequence of observations), exposures or events
    PHOT("phot"), //  Photometry
    PHOT_ANTENNATEMP("phot.antennaTemp"), //  Antenna temperature
    PHOT_CALIB("phot.calib"), //  Photometric calibration
    PHOT_COLOR("phot.color"), //  Color index or magnitude difference
    PHOT_COLOR_EXCESS("phot.color.excess"), //  Color excess
    PHOT_COLOR_REDDFREE("phot.color.reddFree"), //  Dereddened color
    PHOT_COUNT("phot.count"), //  Flux expressed in counts
    PHOT_FLUENCE("phot.fluence"), //  Radiant photon energy received by a surface per unit area or irradiance of a surface integrated over time of irradiation
    PHOT_FLUX("phot.flux"), //  Photon flux or irradiance
    PHOT_FLUX_BOL("phot.flux.bol"), //  Bolometric flux
    PHOT_FLUX_DENSITY("phot.flux.density"), //  Flux density (per wl/freq/energy interval)
    PHOT_FLUX_DENSITY_SB("phot.flux.density.sb"), //  Flux density surface brightness
    PHOT_FLUX_SB("phot.flux.sb"), //  Flux surface brightness
    PHOT_LIMBDARK("phot.limbDark"), //  Limb-darkening coefficients
    PHOT_MAG("phot.mag"), //  Photometric magnitude
    PHOT_MAG_BC("phot.mag.bc"), //  Bolometric correction
    PHOT_MAG_BOL("phot.mag.bol"), //  Bolometric magnitude
    PHOT_MAG_DISTMOD("phot.mag.distMod"), //  Distance modulus
    PHOT_MAG_REDDFREE("phot.mag.reddFree"), //  Dereddened magnitude
    PHOT_MAG_SB("phot.mag.sb"), //  Surface brightness in magnitude units
    PHOT_RADIANCE("phot.radiance"), //  Radiance as energy flux per solid angle
    PHYS("phys"), //  Physical quantities
    PHYS_SFR("phys.SFR"), //  Star formation rate
    PHYS_ABSORPTION("phys.absorption"), //  Extinction or absorption along the line of sight
    PHYS_ABSORPTION_COEFF("phys.absorption.coeff"), //  Absorption coefficient (e.g. in a spectral line)
    PHYS_ABSORPTION_GAL("phys.absorption.gal"), //  Galactic extinction
    PHYS_ABSORPTION_OPTICALDEPTH("phys.absorption.opticalDepth"), //  Optical depth
    PHYS_ABUND("phys.abund"), //  Abundance
    PHYS_ABUND_FE("phys.abund.Fe"), //  Fe/H abundance
    PHYS_ABUND_X("phys.abund.X"), //  Hydrogen abundance
    PHYS_ABUND_Y("phys.abund.Y"), //  Helium abundance
    PHYS_ABUND_Z("phys.abund.Z"), //  Metallicity abundance
    PHYS_ACCELERATION("phys.acceleration"), //  Acceleration
    PHYS_AEROSOL("phys.aerosol"), //  Relative to aerosol
    PHYS_ALBEDO("phys.albedo"), //  Albedo or reflectance
    PHYS_ANGAREA("phys.angArea"), //  Angular area
    PHYS_ANGMOMENTUM("phys.angMomentum"), //  Angular momentum
    PHYS_ANGSIZE("phys.angSize"), //  Angular size width diameter dimension extension major minor axis extraction radius
    PHYS_ANGSIZE_SMAJAXIS("phys.angSize.smajAxis"), //  Angular size extent or extension of semi-major axis
    PHYS_ANGSIZE_SMINAXIS("phys.angSize.sminAxis"), //  Angular size extent or extension of semi-minor axis
    PHYS_AREA("phys.area"), //  Area (in surface), not angular units)
    PHYS_ATMOL("phys.atmol"), //  Atomic and molecular physics (shared properties)
    PHYS_ATMOL_BRANCHINGRATIO("phys.atmol.branchingRatio"), //  Branching ratio
    PHYS_ATMOL_COLLISIONAL("phys.atmol.collisional"), //  Related to collisions
    PHYS_ATMOL_COLLSTRENGTH("phys.atmol.collStrength"), //  Collisional strength
    PHYS_ATMOL_CONFIGURATION("phys.atmol.configuration"), //  Configuration
    PHYS_ATMOL_CROSSSECTION("phys.atmol.crossSection"), //  Atomic / molecular cross-section
    PHYS_ATMOL_ELEMENT("phys.atmol.element"), //  Element
    PHYS_ATMOL_EXCITATION("phys.atmol.excitation"), //  Atomic molecular excitation parameter
    PHYS_ATMOL_FINAL("phys.atmol.final"), //  Quantity refers to atomic/molecular final/ground state), level), etc.
    PHYS_ATMOL_INITIAL("phys.atmol.initial"), //  Quantity refers to atomic/molecular initial state), level), etc.
    PHYS_ATMOL_IONSTAGE("phys.atmol.ionStage"), //  Ion), ionization stage
    PHYS_ATMOL_IONIZATION("phys.atmol.ionization"), //  Related to ionization
    PHYS_ATMOL_LANDE("phys.atmol.lande"), //  Lande factor
    PHYS_ATMOL_LEVEL("phys.atmol.level"), //  Atomic level
    PHYS_ATMOL_LIFETIME("phys.atmol.lifetime"), //  Lifetime of a level
    PHYS_ATMOL_LINESHIFT("phys.atmol.lineShift"), //  Line shifting coefficient
    PHYS_ATMOL_NUMBER("phys.atmol.number"), //  Atomic number Z
    PHYS_ATMOL_OSCSTRENGTH("phys.atmol.oscStrength"), //  Oscillator strength
    PHYS_ATMOL_PARITY("phys.atmol.parity"), //  Parity
    PHYS_ATMOL_QN("phys.atmol.qn"), //  Quantum number
    PHYS_ATMOL_RADIATIONTYPE("phys.atmol.radiationType"), //  Type of radiation characterizing atomic lines (electric dipole/quadrupole), magnetic dipole)
    PHYS_ATMOL_SYMMETRY("phys.atmol.symmetry"), //  Type of nuclear spin symmetry
    PHYS_ATMOL_SWEIGHT("phys.atmol.sWeight"), //  Statistical weight
    PHYS_ATMOL_SWEIGHT_NUCLEAR("phys.atmol.sWeight.nuclear"), //  Statistical weight for nuclear spin states
    PHYS_ATMOL_TERM("phys.atmol.term"), //  Atomic term
    PHYS_ATMOL_TRANSITION("phys.atmol.transition"), //  Transition between states
    PHYS_ATMOL_TRANSPROB("phys.atmol.transProb"), //  Transition probability), Einstein A coefficient
    PHYS_ATMOL_WOSCSTRENGTH("phys.atmol.wOscStrength"), //  Weighted oscillator strength
    PHYS_ATMOL_WEIGHT("phys.atmol.weight"), //  Atomic weight
    PHYS_COLUMNDENSITY("phys.columnDensity"), //  Column density
    PHYS_COMPOSITION("phys.composition"), //  Quantities related to composition of objects
    PHYS_COMPOSITION_MASSLIGHTRA("phys.composition.massLightRatio"), //  Mass to light ratio
    PHYS_COMPOSITION_YIELD("phys.composition.yield"), //  Mass yield
    PHYS_COSMOLOGY("phys.cosmology"), //  Related to cosmology
    PHYS_DAMPING("phys.damping"), //  Generic damping quantities
    PHYS_DENSITY("phys.density"), //  Density (of mass), electron), ...)
    PHYS_DENSITY_PHASESPACE("phys.density.phaseSpace"), //  Density in the phase space
    PHYS_DIELECTRIC("phys.dielectric"), //  Complex dielectric function
    PHYS_DISPMEASURE("phys.dispMeasure"), //  Dispersion measure
    PHYS_DUST("phys.dust"), //  Relative to dust
    PHYS_ELECTFIELD("phys.electField"), //  Electric field
    PHYS_ELECTRON("phys.electron"), //  Electron
    PHYS_ELECTRON_DEGEN("phys.electron.degen"), //  Electron degeneracy parameter
    PHYS_EMISSMEASURE("phys.emissMeasure"), //  Emission measure
    PHYS_EMISSIVITY("phys.emissivity"), //  Emissivity
    PHYS_ENERGY("phys.energy"), //  Energy
    PHYS_ENERGY_GIBBS("phys.energy.Gibbs"), //  Gibbs (free) energy or free enthalpy [G=H-TS]
    PHYS_ENERGY_HELMHOLTZ("phys.energy.Helmholtz"), //  Helmholtz free energy [A=U-TS]
    PHYS_ENERGY_DENSITY("phys.energy.density"), //  Energy density
    PHYS_ENTHALPY("phys.enthalpy"), //  Enthalpy [H=U+pv]
    PHYS_ENTROPY("phys.entropy"), //  Entropy
    PHYS_EOS("phys.eos"), //  Equation of state
    PHYS_EXCITPARAM("phys.excitParam"), //  Excitation parameter U
    PHYS_FLUENCE("phys.fluence"), //  Particle energy received by a surface per unit area integrated over time
    PHYS_FLUX("phys.flux"), //  Flux or flow of particle), energy), etc.
    PHYS_FLUX_ENERGY("phys.flux.energy"), //  Energy flux), heat flux
    PHYS_GAUNTFACTOR("phys.gauntFactor"), //  Gaunt factor/correction
    PHYS_GRAVITY("phys.gravity"), //  Gravity
    PHYS_IONIZPARAM("phys.ionizParam"), //  Ionization parameter
    PHYS_IONIZPARAM_COLL("phys.ionizParam.coll"), //  Collisional ionization
    PHYS_IONIZPARAM_RAD("phys.ionizParam.rad"), //  Radiative ionization
    PHYS_LUMINOSITY("phys.luminosity"), //  Luminosity
    PHYS_LUMINOSITY_FUN("phys.luminosity.fun"), //  Luminosity function
    PHYS_MAGABS("phys.magAbs"), //  Absolute magnitude
    PHYS_MAGABS_BOL("phys.magAbs.bol"), //  Bolometric absolute magnitude
    PHYS_MAGFIELD("phys.magField"), //  Magnetic field
    PHYS_MASS("phys.mass"), //  Mass
    PHYS_MASS_INERTIAMOMENTUM("phys.mass.inertiaMomentum"), //  Momentum of inertia or rotational inertia
    PHYS_MASS_LOSS("phys.mass.loss"), //  Mass loss
    PHYS_MOL("phys.mol"), //  Molecular data
    PHYS_MOL_DIPOLE("phys.mol.dipole"), //  Molecular dipole
    PHYS_MOL_DIPOLE_ELECTRIC("phys.mol.dipole.electric"), //  Molecular electric dipole moment
    PHYS_MOL_DIPOLE_MAGNETIC("phys.mol.dipole.magnetic"), //  Molecular magnetic dipole moment
    PHYS_MOL_DISSOCIATION("phys.mol.dissociation"), //  Molecular dissociation
    PHYS_MOL_FORMATIONHEAT("phys.mol.formationHeat"), //  Formation heat for molecules
    PHYS_MOL_QUADRUPOLE("phys.mol.quadrupole"), //  Molecular quadrupole
    PHYS_MOL_QUADRUPOLE_ELECTRIC("phys.mol.quadrupole.electric"), //  Molecular electric quadrupole moment
    PHYS_MOL_ROTATION("phys.mol.rotation"), //  Molecular rotation
    PHYS_MOL_VIBRATION("phys.mol.vibration"), //  Molecular vibration
    PHYS_PARTICLE("phys.particle"), //  Related to physical particles
    PHYS_PARTICLE_NEUTRINO("phys.particle.neutrino"), //  Related to neutrino
    PHYS_PARTICLE_NEUTRON("phys.particle.neutron"), //  Related to neutron
    PHYS_PARTICLE_PROTON("phys.particle.proton"), //  Related to proton
    PHYS_PARTICLE_ALPHA("phys.particle.alpha"), //  Related to alpha particle
    PHYS_PHASESPACE("phys.phaseSpace"), //  Related to phase space
    PHYS_POLARIZATION("phys.polarization"), //  Polarization degree (or percentage)
    PHYS_POLARIZATION_CIRCULAR("phys.polarization.circular"), //  Circular polarization
    PHYS_POLARIZATION_LINEAR("phys.polarization.linear"), //  Linear polarization
    PHYS_POLARIZATION_ROTMEASURE("phys.polarization.rotMeasure"), //  Rotation measure polarization
    PHYS_POLARIZATION_STOKES("phys.polarization.stokes"), //  Stokes polarization
    PHYS_POLARIZATION_STOKES_I("phys.polarization.stokes.I"), //  Stokes polarization coefficient I
    PHYS_POLARIZATION_STOKES_Q("phys.polarization.stokes.Q"), //  Stokes polarization coefficient Q
    PHYS_POLARIZATION_STOKES_U("phys.polarization.stokes.U"), //  Stokes polarization coefficient U
    PHYS_POLARIZATION_STOKES_V("phys.polarization.stokes.V"), //  Stokes polarization coefficient V
    PHYS_POTENTIAL("phys.potential"), //  Potential (electric), gravitational), etc)
    PHYS_PRESSURE("phys.pressure"), //  Pressure
    PHYS_RECOMBINATION_COEFF("phys.recombination.coeff"), //  Recombination coefficient
    PHYS_REFRACTINDEX("phys.refractIndex"), //  Refraction index
    PHYS_SIZE("phys.size"), //  Linear size), length (not angular)
    PHYS_SIZE_AXISRATIO("phys.size.axisRatio"), //  Axis ratio (a/b) or (b/a)
    PHYS_SIZE_DIAMETER("phys.size.diameter"), //  Diameter
    PHYS_SIZE_RADIUS("phys.size.radius"), //  Radius
    PHYS_SIZE_SMAJAXIS("phys.size.smajAxis"), //  Linear semi major axis
    PHYS_SIZE_SMINAXIS("phys.size.sminAxis"), //  Linear semi minor axis
    PHYS_SIZE_SMEDAXIS("phys.size.smedAxis"), //  Linear semi median axis for 3D ellipsoids
    PHYS_TEMPERATURE("phys.temperature"), //  Temperature
    PHYS_TEMPERATURE_EFFECTIVE("phys.temperature.effective"), //  Effective temperature
    PHYS_TEMPERATURE_ELECTRON("phys.temperature.electron"), //  Electron temperature
    PHYS_TRANSMISSION("phys.transmission"), //  Transmission (of filter), instrument), ...)
    PHYS_VELOC("phys.veloc"), //  Space velocity
    PHYS_VELOC_ANG("phys.veloc.ang"), //  Angular velocity
    PHYS_VELOC_DISPERSION("phys.veloc.dispersion"), //  Velocity dispersion
    PHYS_VELOC_ESCAPE("phys.veloc.escape"), //  Escape velocity
    PHYS_VELOC_EXPANSION("phys.veloc.expansion"), //  Expansion velocity
    PHYS_VELOC_MICROTURB("phys.veloc.microTurb"), //  Microturbulence velocity
    PHYS_VELOC_ORBITAL("phys.veloc.orbital"), //  Orbital velocity
    PHYS_VELOC_PULSAT("phys.veloc.pulsat"), //  Pulsational velocity
    PHYS_VELOC_ROTAT("phys.veloc.rotat"), //  Rotational velocity
    PHYS_VELOC_TRANSVERSE("phys.veloc.transverse"), //  Transverse / tangential velocity
    PHYS_VIRIAL("phys.virial"), //  Related to virial quantities (mass), radius), ...)
    PHYS_VOLUME("phys.volume"), //  Volume (in cubic units)
    POS("pos"), //  Position and coordinates
    POS_ANGDISTANCE("pos.angDistance"), //  Angular distance), elongation
    POS_ANGRESOLUTION("pos.angResolution"), //  Angular resolution
    POS_AZ("pos.az"), //  Position in alt-azimuth frame
    POS_AZ_ALT("pos.az.alt"), //  Alt-azimuth altitude
    POS_AZ_AZI("pos.az.azi"), //  Alt-azimuth azimuth
    POS_AZ_ZD("pos.az.zd"), //  Alt-azimuth zenith distance
    POS_BARYCENTER("pos.barycenter"), //  Barycenter
    POS_BODYRC("pos.bodyrc"), //  Body related coordinates
    POS_BODYRC_ALT("pos.bodyrc.alt"), //  Body related coordinate (altitude on the body)
    POS_BODYRC_LAT("pos.bodyrc.lat"), //  Body related coordinate (latitude on the body)
    POS_BODYRC_LON("pos.bodyrc.lon"), //  Body related coordinate (longitude on the body)
    POS_CARTESIAN("pos.cartesian"), //  Cartesian (rectangular) coordinates
    POS_CARTESIAN_X("pos.cartesian.x"), //  Cartesian coordinate along the x-axis
    POS_CARTESIAN_Y("pos.cartesian.y"), //  Cartesian coordinate along the y-axis
    POS_CARTESIAN_Z("pos.cartesian.z"), //  Cartesian coordinate along the z-axis
    POS_CENTROID("pos.centroid"), //  Related to the centroid of a measure.
    POS_CMB("pos.cmb"), //  Cosmic Microwave Background reference frame
    POS_DIRCOS("pos.dirCos"), //  Direction cosine
    POS_DISTANCE("pos.distance"), //  Linear distance
    POS_EARTH("pos.earth"), //  Coordinates related to Earth
    POS_EARTH_ALTITUDE("pos.earth.altitude"), //  Altitude), height on Earth  above sea level
    POS_EARTH_LAT("pos.earth.lat"), //  Latitude on Earth
    POS_EARTH_LON("pos.earth.lon"), //  Longitude on Earth
    POS_ECLIPTIC("pos.ecliptic"), //  Ecliptic coordinates
    POS_ECLIPTIC_LAT("pos.ecliptic.lat"), //  Ecliptic latitude
    POS_ECLIPTIC_LON("pos.ecliptic.lon"), //  Ecliptic longitude
    POS_EOP("pos.eop"), //  Earth orientation parameters
    POS_EPHEM("pos.ephem"), //  Ephemeris
    POS_EQ("pos.eq"), //  Equatorial coordinates
    POS_EQ_DEC("pos.eq.dec"), //  Declination in equatorial coordinates
    POS_EQ_HA("pos.eq.ha"), //  Hour-angle
    POS_EQ_RA("pos.eq.ra"), //  Right ascension in equatorial coordinates
    POS_EQ_SPD("pos.eq.spd"), //  South polar distance in equatorial coordinates
    POS_ERRORELLIPSE("pos.errorEllipse"), //  Positional error ellipse
    POS_FRAME("pos.frame"), //  Reference frame used for positions
    POS_GALACTIC("pos.galactic"), //  Galactic coordinates
    POS_GALACTIC_LAT("pos.galactic.lat"), //  Latitude in galactic coordinates
    POS_GALACTIC_LON("pos.galactic.lon"), //  Longitude in galactic coordinates
    POS_GALACTOCENTRIC("pos.galactocentric"), //  Galactocentric coordinate system
    POS_GEOCENTRIC("pos.geocentric"), //  Geocentric coordinate system
    POS_HEALPIX("pos.healpix"), //  Hierarchical Equal Area IsoLatitude Pixelization
    POS_HELIOCENTRIC("pos.heliocentric"), //  Heliocentric position coordinate (solar system bodies)
    POS_HTM("pos.HTM"), //  Hierarchical Triangular Mesh
    POS_LAMBERT("pos.lambert"), //  Lambert projection
    POS_LG("pos.lg"), //  Local Group reference frame
    POS_LSR("pos.lsr"), //  Local Standard of Rest reference frame
    POS_LUNAR("pos.lunar"), //  Lunar coordinates
    POS_LUNAR_OCCULT("pos.lunar.occult"), //  Occultation by lunar limb
    POS_NUTATION("pos.nutation"), //  Nutation (of a body)
    POS_OUTLINE("pos.outline"), //  Set of points outlining a region (contour)
    POS_PARALLAX("pos.parallax"), //  Parallax
    POS_PARALLAX_DYN("pos.parallax.dyn"), //  Dynamical parallax
    POS_PARALLAX_PHOT("pos.parallax.phot"), //  Photometric parallaxes
    POS_PARALLAX_SPECT("pos.parallax.spect"), //  Spectroscopic parallax
    POS_PARALLAX_TRIG("pos.parallax.trig"), //  Trigonometric parallax
    POS_PHASEANG("pos.phaseAng"), //  Phase angle), e.g. elongation of earth from sun as seen from a third celestial object
    POS_PM("pos.pm"), //  Proper motion
    POS_POSANG("pos.posAng"), //  Position angle of a given vector
    POS_PRECESS("pos.precess"), //  Precession (in equatorial coordinates)
    POS_SUPERGALACTIC("pos.supergalactic"), //  Supergalactic coordinates
    POS_SUPERGALACTIC_LAT("pos.supergalactic.lat"), //  Latitude in supergalactic coordinates
    POS_SUPERGALACTIC_LON("pos.supergalactic.lon"), //  Longitude in supergalactic coordinates
    POS_WCS("pos.wcs"), //  WCS keywords
    POS_WCS_CDMATRIX("pos.wcs.cdmatrix"), //  WCS CDMATRIX
    POS_WCS_CRPIX("pos.wcs.crpix"), //  WCS CRPIX
    POS_WCS_CRVAL("pos.wcs.crval"), //  WCS CRVAL
    POS_WCS_CTYPE("pos.wcs.ctype"), //  WCS CTYPE
    POS_WCS_NAXES("pos.wcs.naxes"), //  WCS NAXES
    POS_WCS_NAXIS("pos.wcs.naxis"), //  WCS NAXIS
    POS_WCS_SCALE("pos.wcs.scale"), //  WCS scale or scale of an image
    SPECT("spect"), //  Spectroscopy
    SPECT_BINSIZE("spect.binSize"), //  Spectral bin size
    SPECT_CONTINUUM("spect.continuum"), //  Continuum spectrum
    SPECT_DOPPLERPARAM("spect.dopplerParam"), //  Doppler parameter b
    SPECT_DOPPLERVELOC("spect.dopplerVeloc"), //  Radial velocity), derived from the shift of some spectral feature
    SPECT_DOPPLERVELOC_OPT("spect.dopplerVeloc.opt"), //  Radial velocity derived from a wavelength shift using the optical convention
    SPECT_DOPPLERVELOC_RADIO("spect.dopplerVeloc.radio"), //  Radial velocity derived from a frequency shift using the radio convention
    SPECT_INDEX("spect.index"), //  Spectral index
    SPECT_LINE("spect.line"), //  Spectral line
    SPECT_LINE_ASYMMETRY("spect.line.asymmetry"), //  Line asymmetry
    SPECT_LINE_BROAD("spect.line.broad"), //  Spectral line broadening
    SPECT_LINE_BROAD_STARK("spect.line.broad.Stark"), //  Stark line broadening coefficient
    SPECT_LINE_BROAD_ZEEMAN("spect.line.broad.Zeeman"), //  Zeeman broadening
    SPECT_LINE_EQWIDTH("spect.line.eqWidth"), //  Line equivalent width
    SPECT_LINE_INTENSITY("spect.line.intensity"), //  Line intensity
    SPECT_LINE_PROFILE("spect.line.profile"), //  Line profile
    SPECT_LINE_STRENGTH("spect.line.strength"), //  Spectral line strength S
    SPECT_LINE_WIDTH("spect.line.width"), //  Spectral line full width half maximum
    SPECT_RESOLUTION("spect.resolution"), //  Spectral (or velocity) resolution
    SRC("src"), //  Observed source viewed on the sky
    SRC_CALIB("src.calib"), //  Calibration source
    SRC_CALIB_GUIDESTAR("src.calib.guideStar"), //  Guide star
    SRC_CLASS("src.class"), //  Source classification (star), galaxy), cluster), comet), asteroid )
    SRC_CLASS_COLOR("src.class.color"), //  Color classification
    SRC_CLASS_DISTANCE("src.class.distance"), //  Distance class e.g. Abell
    SRC_CLASS_LUMINOSITY("src.class.luminosity"), //  Luminosity class
    SRC_CLASS_RICHNESS("src.class.richness"), //  Richness class e.g. Abell
    SRC_CLASS_STARGALAXY("src.class.starGalaxy"), //  Star/galaxy discriminator), stellarity index
    SRC_CLASS_STRUCT("src.class.struct"), //  Structure classification e.g. Bautz-Morgan
    SRC_DENSITY("src.density"), //  Density of sources
    SRC_ELLIPTICITY("src.ellipticity"), //  Source ellipticity
    SRC_IMPACTPARAM("src.impactParam"), //  Impact parameter
    SRC_MORPH("src.morph"), //  Morphology structure
    SRC_MORPH_PARAM("src.morph.param"), //  Morphological parameter
    SRC_MORPH_SCLENGTH("src.morph.scLength"), //  Scale length for a galactic component (disc or bulge)
    SRC_MORPH_TYPE("src.morph.type"), //  Hubble morphological type (galaxies)
    SRC_NET("src.net"), //  Qualifier indicating that a quantity (e.g. flux) is background subtracted rather than total
    SRC_ORBITAL("src.orbital"), //  Orbital parameters
    SRC_ORBITAL_ECCENTRICITY("src.orbital.eccentricity"), //  Orbit eccentricity
    SRC_ORBITAL_INCLINATION("src.orbital.inclination"), //  Orbit inclination
    SRC_ORBITAL_MEANANOMALY("src.orbital.meanAnomaly"), //  Orbit mean anomaly
    SRC_ORBITAL_MEANMOTION("src.orbital.meanMotion"), //  Mean motion
    SRC_ORBITAL_NODE("src.orbital.node"), //  Ascending node
    SRC_ORBITAL_PERIASTRON("src.orbital.periastron"), //  Periastron
    SRC_ORBITAL_TISSERAND("src.orbital.Tisserand"), //  Tisserand parameter (generic)
    SRC_ORBITAL_TISSJ("src.orbital.TissJ"), //  Tisserand parameter with respect to Jupiter
    SRC_REDSHIFT("src.redshift"), //  Redshift
    SRC_REDSHIFT_PHOT("src.redshift.phot"), //  Photometric redshift
    SRC_SAMPLE("src.sample"), //  Sample
    SRC_SPTYPE("src.spType"), //  Spectral type MK
    SRC_VAR("src.var"), //  Variability of source
    SRC_VAR_AMPLITUDE("src.var.amplitude"), //  Amplitude of variation
    SRC_VAR_INDEX("src.var.index"), //  Variability index
    SRC_VAR_PULSE("src.var.pulse"), //  Pulse
    STAT("stat"), //  Statistical parameters
    STAT_ASYMMETRY("stat.asymmetry"), //  Measure of asymmetry
    STAT_CORRELATION("stat.correlation"), //  Correlation between two parameters
    STAT_COVARIANCE("stat.covariance"), //  Covariance between two parameters
    STAT_ERROR("stat.error"), //  Statistical error
    STAT_ERROR_SYS("stat.error.sys"), //  Systematic error
    STAT_FILLING("stat.filling"), //  Filling factor (volume), time), ...)
    STAT_FIT("stat.fit"), //  Fit
    STAT_FIT_CHI2("stat.fit.chi2"), //  Chi2
    STAT_FIT_DOF("stat.fit.dof"), //  Degrees of freedom
    STAT_FIT_GOODNESS("stat.fit.goodness"), //  Goodness or significance of fit
    STAT_FIT_OMC("stat.fit.omc"), //  Observed minus computed
    STAT_FIT_PARAM("stat.fit.param"), //  Parameter of fit
    STAT_FIT_RESIDUAL("stat.fit.residual"), //  Residual fit
    STAT_FOURIER("stat.Fourier"), //  Fourier coefficient
    STAT_FOURIER_AMPLITUDE("stat.Fourier.amplitude"), //  Amplitude of Fourier coefficient
    STAT_FWHM("stat.fwhm"), //  Full width at half maximum
    STAT_INTERVAL("stat.interval"), //  Generic interval between two limits (defined as a pair of values)
    STAT_LIKELIHOOD("stat.likelihood"), //  Likelihood
    STAT_MAX("stat.max"), //  Maximum or upper limit
    STAT_MEAN("stat.mean"), //  Mean), average value
    STAT_MEDIAN("stat.median"), //  Median value
    STAT_MIN("stat.min"), //  Minimum or lowest limit
    STAT_PARAM("stat.param"), //  Parameter
    STAT_PROBABILITY("stat.probability"), //  Probability
    STAT_RANK("stat.rank"), //  Rank or order in list of sorted values
    STAT_RMS("stat.rms"), //  Root mean square as square root of sum of squared values or quadratic mean
    STAT_SNR("stat.snr"), //  Signal to noise ratio
    STAT_STDEV("stat.stdev"), //  Standard deviation as the square root of the variance
    STAT_UNCALIB("stat.uncalib"), //  Qualifier of a generic uncalibrated quantity
    STAT_VALUE("stat.value"), //  Miscellaneous value
    STAT_VARIANCE("stat.variance"), //  Variance
    STAT_WEIGHT("stat.weight"), //  Statistical weight
    TIME("time"), //  Time), generic quantity in units of time or date
    TIME_AGE("time.age"), //  Age
    TIME_CREATION("time.creation"), //  Creation time/date (of dataset), file), catalogue),...)
    TIME_CROSSING("time.crossing"), //  Crossing time
    TIME_DURATION("time.duration"), //  Interval of time describing the duration of a generic event or phenomenon
    TIME_END("time.end"), //  End time/date of a generic event
    TIME_EPOCH("time.epoch"), //  Instant of time related to a generic event (epoch), date), Julian date), time stamp/tag),...)
    TIME_EQUINOX("time.equinox"), //  Equinox
    TIME_INTERVAL("time.interval"), //  Time interval), time-bin), time elapsed between two events), not the duration of an event
    TIME_LIFETIME("time.lifetime"), //  Lifetime
    TIME_PERIOD("time.period"), //  Period), interval of time between the recurrence of phases in a periodic phenomenon
    TIME_PERIOD_REVOLUTION("time.period.revolution"), //  Period of revolution of a body around a primary one (similar to year)
    TIME_PERIOD_ROTATION("time.period.rotation"), //  Period of rotation of a body around its axis (similar to day)
    TIME_PHASE("time.phase"), //  Phase), position within a period
    TIME_PROCESSING("time.processing"), //  A time/date associated with the processing of data
    TIME_PUBLIYEAR("time.publiYear"), //  Publication year
    TIME_RELAX("time.relax"), //  Relaxation time
    TIME_RELEASE("time.release"), //  The time/date data is available to the public
    TIME_RESOLUTION("time.resolution"), //  Time resolution
    TIME_SCALE("time.scale"), //  Timescale
    TIME_START("time.start"); //  Start time/date of generic event


    private final String value;

    private boolean negative = false;

    UCD(String value) {
        this.value = value;
    }

    /**
     *  UCD value that should not exist to be considered a match.
     *
     * @return The UCD object.
     */
    public UCD negative() {
        this.negative = true;
        return this;
    }

    public UCD positive() {
        this.negative = false;
        return this;
    }

    public String getValue() {
        return value;
    }

    public boolean matches(String str) {
        boolean isMatch = str.toLowerCase().contains(value.toLowerCase());
        return negative != isMatch;
    }

    public static boolean isMain(String str) {
        return str.toLowerCase().contains(UCD.META_MAIN.getValue());
    }

    @Override
    public String toString() {
        return value;
    }

    public boolean isNegative() {
        return negative;
    }
}