package esac.archive.esasky.cl.wcstransform.module.jskycoords;

/**
 * WCS - World Coordinate System
 */
public class WCSTransform {

    double xref; // X reference coordinate value (deg)
    double yref; // Y reference coordinate value (deg)
    double xrefpix; // X reference pixel
    double yrefpix; // Y reference pixel
    double xinc; // X coordinate increment (deg)
    double yinc; // Y coordinate increment (deg)
    double rot; // rotation around opt. axis (deg) (N through E)
    double crot, srot; // Cosine and sine of rotation angle
    double cd11, cd12, cd21, cd22; // rotation matrix
    double dc11, dc12, dc21, dc22; // inverse rotation matrix
    double mrot; // Chip rotation angle (deg) (N through E)
    double cmrot, smrot; // Cosine and sine of chip rotation angle
    double xmpix, ympix; // X and Y center for chip rotation
    double equinox; // Equinox of coordinates default to 1950.0
    double epoch; // Epoch of coordinates default to equinox
    double nxpix; // Number of pixels in X-dimension of image
    double nypix; // Number of pixels in Y-dimension of image
    double plate_ra; // Right ascension of plate center
    double plate_dec; // Declination of plate center
    double plate_scale; // Plate scale in arcsec/mm
    double x_pixel_offset; // X pixel offset of image lower right
    double y_pixel_offset; // Y pixel offset of image lower right
    double x_pixel_size; // X pixel_size
    double y_pixel_size; // Y pixel_size
    double ppo_coeff[] = new double[6];
    double amd_x_coeff[] = new double[20]; // X coefficients for plate model
    double amd_y_coeff[] = new double[20]; // Y coefficients for plate model
    double xpix; // X (RA) coordinate (pixels)
    double ypix; // Y (dec) coordinate (pixels)
    double xpos; // X (RA) coordinate (deg)
    double ypos; // Y (dec) coordinate (deg)
    int pcode; // projection code (-1-8)
    int changesys; // 1 for FK4->FK5, 2 for FK5->FK4
    // 3 for FK4->galactic, 4 for FK5->galactic
    int printsys; // 1 to print coordinate system, else 0
    int ndec; // Number of decimal places in PIX2WCST
    int degout; // 1 to always print degrees in PIX2WCST
    int tabsys; // 1 to put tab between RA & Dec, else 0
    int rotmat; // 0 if CDELT, CROTA; 1 if CD
    int coorflip; // 0 if x=RA, y=Dec; 1 if x=Dec, y=RA
    int offscl; // 0 if OK, 1 if offscale
    int plate_fit; // 1 if plate fit, else 0
    int wcson; // 1 if WCS is set, else 0
    int detector; // Instrument detector number
    String instrument = "";// Instrument name
    String c1type = ""; // 1st coordinate type code: RA--, GLON, ELON
    String c2type = ""; // 2nd coordinate type code: DEC-, GLAT, ELAT
    String[] ctypes = { "-SIN", "-TAN", "-ARC", "-NCP", "-GLS", "-MER", "-AIT", "-STG" };
    String ptype = ""; // projection type code (on of the ctype values)
    String radecsys = ""; // Reference frame: FK4, FK4-NO-E, FK5, GAPPT
    String sysout = ""; // Reference frame for output: FK4, FK5
    String center = ""; // Center coordinates (with frame)

    // These fields added for convenience
    double fCenterRa;
    double fCenterDec;
    double fHalfWidthRa;
    double fHalfWidthDec;
    double fWidthDeg;
    double fHeightDeg;

    // allan: added for quick access to degrees per pixel
    Point2D.Double degPerPixel;

    /**
     * Constructs a new WCSTransform.
     *
     * @param cra Center right ascension in degrees
     * @param cdec Center declination in degrees
     * @param xsecpix Number of arcseconds per pixel along x-axis
     * @param ysecpix Number of arcseconds per pixel along y-axis
     * @param xrpix Reference pixel X coordinate
     * @param yrpix Reference pixel Y coordinate
     * @param nxpix Number of pixels along x-axis
     * @param nypix Number of pixels along y-axis
     * @param rotate Rotation angle (clockwise positive) in degrees
     * @param equinox Equinox of coordinates, 1950 and 2000 supported
     * @param epoch Epoch of coordinates, used for FK4/FK5 conversion no effect if 0
     * @param proj Projection
     */
    public WCSTransform(double cra, double cdec, double xsecpix, double ysecpix, double xrpix,
            double yrpix, int nxpix, int nypix, double rotate, int equinox, double epoch,
            String proj) {
        super();

        // Plate solution coefficients
        this.plate_fit = 0;
        this.nxpix = nxpix;
        this.nypix = nypix;

        // Approximate world coordinate system from a known plate scale
        this.xinc = xsecpix / 3600.0;
        this.yinc = ysecpix / 3600.0;
        this.xrefpix = xrpix;
        this.yrefpix = yrpix;

        this.xref = cra;
        this.yref = cdec;
        this.c1type = "RA-";
        this.c2type = "DEC";
        this.ptype = proj;
        this.pcode = 1;
        this.coorflip = 0;
        this.rot = rotate;
        this.rotmat = 0;
        this.cd11 = 0.0;
        this.cd21 = 0.0;
        this.cd12 = 0.0;
        this.cd22 = 0.0;
        this.dc11 = 0.0;
        this.dc21 = 0.0;
        this.dc12 = 0.0;
        this.dc22 = 0.0;

        // Coordinate reference frame and equinox
        this.equinox = equinox;
        if (equinox > 1980) {
            this.radecsys = "FK5";
        } else {
            this.radecsys = "FK4";
        }
        if (epoch > 0) {
            this.epoch = epoch;
        } else {
            this.epoch = 0.0;
        }
        this.wcson = 1;

        this.sysout = this.radecsys;
        this.changesys = 0;
        this.printsys = 1;
        this.tabsys = 0;

        // wcsfull(); // allan: added this line 4/24/00; Chris S. moved this line to after the patch

        // Patch. PACS Simulation Working Group; 25 Aug 2002
        // pcode is set to 1 above, but never updated to the value
        // corresponding to the projection type.

        // Find projection type
        this.pcode = 0; // default type is linear
        for (int i = 0; i < 8; i++) {
            if (this.ptype.startsWith(ctypes[i])) {
                this.pcode = i + 1;
            }
        }
        // end of patch

        wcsfull(); // Chris S. moved this line from before the patch
    }

    /**
     * Conversions among hours of RA, degrees and radians.
     */
    public static double degrad(double x) {
        return ((x) * Math.PI / 180.0);
    }

    public static double raddeg(double x) {
        return ((x) * 180.0 / Math.PI);
    }

    public boolean isValid() {
        return wcson > 0;
    }

    /**
     * Converts pixel coordinates to World Coordinates. Returns null if the WCSTransform is not
     * valid.
     */
    public Point2D.Double pix2wcs(double xpix, double ypix) {
        Point2D.Double position;

        if (!isValid()) {
            return null;
        }

        this.xpix = xpix;
        this.ypix = ypix;
        this.offscl = 0;
        // Convert image coordinates to sky coordinates
        if (this.plate_fit > 0) {
            if ((position = platepos.getPosition(xpix, ypix, this)) == null) {
                this.offscl = 1;
            }
        } else if ((position = worldpos.getPosition(xpix, ypix, this)) == null) {
            this.offscl = 1;
        }

        if (this.pcode > 0) {
            // Convert coordinates to FK4 or FK5
            if (this.radecsys.startsWith("FK4")) {
                if (this.equinox != 1950.0) {
                    position = wcscon.fk4prec(this.equinox, 1950.0, position);
                }
            } else if (this.radecsys.startsWith("FK5")) {
                if (this.equinox != 2000.0) {
                    position = wcscon.fk5prec(this.equinox, 2000.0, position);
                }
            }
            // Convert coordinates to desired output system
            if (this.changesys == 1) {
                position = wcscon.fk425e(position, this.epoch);
            } else if (this.changesys == 2) {
                position = wcscon.fk524e(position, this.epoch);
            } else if (this.changesys == 3) {
                position = wcscon.fk42gal(position);
            } else if (this.changesys == 4) {
                position = wcscon.fk52gal(position);
            }
        }
        // System.out.println("offscl " + this.offscl);
        if (this.offscl == 0 && position != null) {
            this.xpos = position.x;
            this.ypos = position.y;
        }

        return position;
    }

    /**
     * Set the RA and Dec of the image center, plus size in degrees
     */
    protected void wcsfull() {
        double xpix;
        double ypix;

        // Find right ascension and declination of coordinates
        if (isValid()) {
            xpix = 0.5 * this.nxpix;
            ypix = 0.5 * this.nypix;

            Point2D.Double center = pix2wcs(xpix, ypix);
            if (center == null) {
                return;
            }

            fCenterRa = center.x;
            fCenterDec = center.y;

            // Compute image width in degrees

            // Chris S. changed 1.0 to 0.0
            // Point2D.Double pos1 = pix2wcs(1.0, ypix);
            Point2D.Double pos1 = pix2wcs(0.0, ypix);
            Point2D.Double pos2 = pix2wcs(this.nxpix, ypix);
            if (pos1 == null || pos2 == null) {
                return;
            }

            if (!this.ptype.startsWith("LINEAR") && !this.ptype.startsWith("PIXEL")) {
                fWidthDeg = wcsdist(pos1.x, pos1.y, pos2.x, pos2.y);
            } else {
                fWidthDeg = Math.sqrt(((pos2.y - pos1.y) * (pos2.y - pos1.y))
                        + ((pos2.x - pos1.x) * (pos2.x - pos1.x)));
            }

            // Compute image height in degrees

            // Chris S. changed 1.0 to 0.0
            // ypos1 = pix2wcs(xpix, 1.0);
            pos1 = pix2wcs(xpix, 0.0);
            pos2 = pix2wcs(xpix, this.nypix);
            if (pos1 == null || pos2 == null) {
                return;
            }

            if (!this.ptype.startsWith("LINEAR") && !this.ptype.startsWith("PIXEL")) {
                fHeightDeg = wcsdist(pos1.x, pos1.y, pos2.x, pos2.y);
            } else {
                fHeightDeg = Math.sqrt(((pos2.y - pos1.y) * (pos2.y - pos1.y))
                        + ((pos2.x - pos1.x) * (pos2.x - pos1.x)));
            }
            degPerPixel = new Point2D.Double(fWidthDeg / nxpix, fHeightDeg / nypix);
        }
    }

    /**
     * Compute distance in degrees between two sky coordinates (RA,Dec) or (Long,Lat) in degrees
     */
    public double wcsdist(double x1, double y1, double x2, double y2) {
        double xr1, xr2, yr1, yr2;
        double pos1[] = new double[3], pos2[] = new double[3], w, diff, cosb;
        int i;

        // Convert two vectors to direction cosines
        xr1 = degrad(x1);
        yr1 = degrad(y1);
        cosb = Math.cos(yr1);
        pos1[0] = Math.cos(xr1) * cosb;
        pos1[1] = Math.sin(xr1) * cosb;
        pos1[2] = Math.sin(yr1);

        xr2 = degrad(x2);
        yr2 = degrad(y2);
        cosb = Math.cos(yr2);
        pos2[0] = Math.cos(xr2) * cosb;
        pos2[1] = Math.sin(xr2) * cosb;
        pos2[2] = Math.sin(yr2);

        // Modulus squared of half the difference vector
        w = 0.0;
        for (i = 0; i < 3; i++) {
            w = w + (pos1[i] - pos2[i]) * (pos1[i] - pos2[i]);
        }

        w = w / 4.0;
        if (w > 1.0) {
            w = 1.0;
        }

        // Angle beween the vectors
        diff = 2.0 * Math.atan2(Math.sqrt(w), Math.sqrt(1.0 - w));
        diff = raddeg(diff);

        return (diff);
    }

}
