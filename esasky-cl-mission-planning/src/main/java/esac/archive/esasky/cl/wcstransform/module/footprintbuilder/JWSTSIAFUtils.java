package esac.archive.esasky.cl.wcstransform.module.footprintbuilder;

import java.math.BigDecimal;

/**
 * Class responsible for conversion between reference frames defined in the SIAF
 * file
 *  @author eracero@sciops.esa.int Copyright (c) 2016 - European Space Agency
 */
public final class JWSTSIAFUtils {

	/**
	 * Protected constructor.
	 */
	protected JWSTSIAFUtils() {

	}

	/**
	 * Calculates the rotation matrix needed for conversion from V-frame
	 * (telescope) coordinates to sky values
	 * 
	 * @param ra
	 *            Input degrees.
	 * @param dec
	 *            Input degrees
	 * @param roll
	 *            Input degrees
	 * @param V2
	 *            coordinate in degrees.
	 * @param V3
	 *            coordinate in degrees.
	 * @return double[][] Rotation Matrix from V-frame (Telescope) to Equatorial
	 *         frame
	 */
	public static BigDecimal[][] getTelescopeToSkyFrameReferenceMatrix(final double ra, final double dec, final double roll,final double v2, final double v3) {
       
    	double raRad = Math.toRadians(ra);
    	double decRad = Math.toRadians(dec);
    	double rollRad = Math.toRadians(roll);
    	double v2Rad = Math.toRadians(v2);
    	double v3Rad = Math.toRadians(v3);
    	
    	//Rotation matrix M1=R2(V3)R3(-v2)
    	BigDecimal[][] M1 = new BigDecimal[3][3];
    	M1[0][0] = new BigDecimal(Math.cos(v2Rad)*Math.cos(v3Rad));
    	M1[0][1] = new BigDecimal(Math.sin(v2Rad)*Math.cos(v3Rad));
    	M1[0][2] = new BigDecimal(Math.sin(v3Rad));
    	M1[1][0] = new BigDecimal(-Math.sin(v2Rad));
    	M1[1][1] = new BigDecimal(Math.cos(v2Rad));
    	M1[1][2] = new BigDecimal(0);
    	M1[2][0] = new BigDecimal(-Math.cos(v2Rad)*Math.sin(v3Rad));
    	M1[2][1] = new BigDecimal(-Math.sin(v2Rad)*Math.sin(v3Rad));
    	M1[2][2] = new BigDecimal(Math.cos(v3Rad));

    	//Rotation matrix M2=R3(ra)R2(-dec)R(-roll)
    	BigDecimal[][] M2 = new BigDecimal[3][3];
    	M2[0][0] = new BigDecimal(Math.cos(raRad)*Math.cos(decRad));
    	M2[0][1] = new BigDecimal(-Math.sin(raRad)*Math.cos(rollRad) + Math.cos(raRad)*Math.sin(decRad)*Math.sin(rollRad));
    	M2[0][2] = new BigDecimal(-Math.sin(raRad)*Math.sin(rollRad) - Math.cos(raRad)*Math.sin(decRad)*Math.cos(rollRad));
    	M2[1][0] = new BigDecimal(Math.sin(raRad)*Math.cos(decRad));
    	M2[1][1] = new BigDecimal(Math.cos(raRad)*Math.cos(rollRad) + Math.sin(raRad)*Math.sin(decRad)*Math.sin(rollRad));
    	M2[1][2] = new BigDecimal(Math.cos(raRad)*Math.sin(rollRad) - Math.sin(raRad)*Math.sin(decRad)*Math.cos(rollRad));
    	M2[2][0] = new BigDecimal(Math.sin(decRad));
    	M2[2][1] = new BigDecimal(-Math.cos(decRad)*Math.sin(rollRad));
    	M2[2][2] = new BigDecimal(Math.cos(decRad)*Math.cos(rollRad));

    	BigDecimal[][] M = multiplyMatrix(M2, M1);
       return M;
    }

	public static double[] convertTelescopeToSkyCoords(double v2, double v3,
			BigDecimal[][] rotationMatrix) {

		/**
		 * Conversion following: w(v2,v3) = M(v20,v30,ra0,dec0,r0)*w'(ra,dec)
		 * where : w1 = (x',y',z') = (cosv2cosv3,sinv2cosv3, sinv3) w0 = (x,y,z)
		 * = (cosracosdec, sinracosdec, sindec) so: ra = atan2(y/x) and
		 * dec=asin(z) All input values expected in degrees)
		 **/

		double v2Rad = Math.toRadians(v2);
		double v3Rad = Math.toRadians(v3);

		BigDecimal x = new BigDecimal(Math.cos(v2Rad) * Math.cos(v3Rad));
		BigDecimal y = new BigDecimal(Math.sin(v2Rad) * Math.cos(v3Rad));
		BigDecimal z = new BigDecimal(Math.sin(v3Rad));

		BigDecimal[][] w1Vector = new BigDecimal[3][1];
		w1Vector[0][0] = x;
		w1Vector[1][0] = y;
		w1Vector[2][0] = z;

		BigDecimal[][] w0Vector = multiplyMatrix(rotationMatrix, w1Vector);


		double ra = Math.toDegrees(Math.atan2(w0Vector[1][0].doubleValue(),
				w0Vector[0][0].doubleValue()));
		double dec = Math.toDegrees(Math.asin(w0Vector[2][0].doubleValue()));

		double[] skycoords = { ra, dec };

		return skycoords;

	}

	public static BigDecimal[][] multiplyMatrix(BigDecimal[][] A,
			BigDecimal[][] B) {

		int aRows = A.length;
		int aColumns = A[0].length;
		int bRows = B.length;
		int bColumns = B[0].length;

		if (aColumns != bRows) {
			throw new IllegalArgumentException("A:Rows: " + aColumns
					+ " did not match B:Columns " + bRows + ".");
		}

		BigDecimal[][] C = new BigDecimal[aRows][bColumns];
		for (int i = 0; i < aRows; i++) {
			for (int j = 0; j < bColumns; j++) {
				C[i][j] = new BigDecimal(0.000000);
			}
		}

		for (int i = 0; i < aRows; i++) { // aRow
			for (int j = 0; j < bColumns; j++) { // bColumn
				for (int k = 0; k < aColumns; k++) { // aColumn
					
					BigDecimal test = (A[i][k]).multiply(B[k][j]);
					C[i][j] = C[i][j].add(test);
				}
			}
		}

		return C;

	}

	public static double[] convertIdealToScienceCoords(double[] coords, double v2ref, double v3ref, double parity, double angle) {
		// xsci -xsciref = idl2sci
		//V3IdlAngle in degrees, convert to radians first
		double v3idlRad = Math.toRadians(angle);
		double xsci = v2ref + parity * coords[0] * Math.cos(v3idlRad) + coords[1] * Math.sin(v3idlRad);
		double ysci = v3ref - parity * coords[0] * Math.sin(v3idlRad) + coords[1] * Math.cos(v3idlRad);

		return new double[] {xsci,ysci};

	}
	
	


}
