
// General config file:
CONFIG = {

  BASE_URL: "//sky.esa.int/esasky-tap",
  SKYIMAGE_BASE_URL: "//sky.esa.int/esasky-tap/skyimage",
  HIPS_SOURCES_ENDPOINT: "/hips-sources",

  COORDS: {
            RA: { DEFAULT: 10.684708, MIN: 0.0, MAX: 359.99999999 },
            DEC: { DEFAULT: 41.26875, MIN: -90.0, MAX: 90.0 }
          },

  DEFAULT_SKY_SURVEY_ID: "DSS2 color",

  ALADIN_DEFAULT_TARGET: "M31",
  FOV: { DEFAULT: 3.76, MIN: 0.0, MAX: 180.0 },
  ASPECT_RATIO: { DEFAULT: 1.79, MIN: 0.25, MAX: 4.0 },
  NORDER: { DEFAULT: 3, MIN: 3, MAX: 9 },
  SIZE: { DEFAULT: 1024, MIN: 300, MAX: 5000 },
  TIMEOUT: { DEFAULT: 15000, MIN: 5000, MAX: 120000 },
  GA_APP_ID: "UA-59849409-6",
  GA_CATEGORY: "SkyImageCutout",
}
