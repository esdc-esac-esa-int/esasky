package esac.archive.esasky.cl.web.client.utility;

public class ProperMotionUtils {

    /* Constants */
    private static final double ZERO = 0.0e0;
    private static final double ONE = 1.0e0;
    private static final double TWO = 2.0e0;
    private static final double PI = 3.14159265358979324e0;
    private static final double RDPDEG = (3.14159265358979324e0/180.0e0);
    private static final double RDPMAS = (3.14159265358979324e0/180.0e0/3600.0e3);
    private static final double EPS = 1.0e-9;
    
    private static double scalar ( double x[], double y[] )
    {
        return ( x[0] * y[0] + x[1] * y[1] + x[2] * y[2] ) ;
    }
    
    
    /*-----------------------------------------------------------------------
    pos_prop

    Propagates the 6-dimensional vector of barycentric astrometric 
    parameters and the associated covariance matrix from epoch t0 to t
    assuming uniform space motion
    
    This is an implementation of the equations in The Hipparcos  
    and Tycho Catalogues (ESA SP-1200), Volume 1, Section 1.5.5,
    'Epoch Transformation: Rigorous Treatment'.
    
    INPUT (all real variables are REALNUMBER):
    
    t0       = original epoch [yr] - see Note 1
    a0[0]    = right ascension at t0 [deg]
    a0[1]    = declination at t0 [deg]
    a0[2]    = parallax at t0 [mas]
    a0[3]    = proper motion in R.A., mult by cos(Dec), at t0 [mas/yr]
    a0[4]    = proper motion in Dec at t0 [mas/yr]
    a0[5]    = normalised radial velocity at t0 [mas/yr] - see Note 2
    t        = new epoch [yr] - see Note 1
    
    
    OUTPUT (all real variables are REALNUMBER):
    
    a[0]    = right ascension at t [deg]
    a[1]    = declination at t [deg]
    a[2]    = parallax at t [mas]
    a[3]    = proper motion in R.A., mult by cos(Dec), at t [mas/yr]
    a[4]    = proper motion in Dec at t [mas/yr]
    a[5]    = normalised radial velocity at t [mas/yr]
    
    FUNCTIONS/SUBROUTINES CALLED:
    
    REALNUMBER FUNCTION scalar = scalar product of two vectors
    
    NOTES:
    
    1. Only t-t0 is used; the origin of the time scale is
    therefore irrelevant but must be the same for t0 and t.
    
    2. The normalised radial velocity at epoch t0 is given by
    a0[5] = vr0*a0[2]/4.740470446
    where vr0 is the barycentric radial velocity in [km/s] at
    epoch t0; similarly a[5] = vr*a[2]/4.740470446 at epoch t.
    
    ------------------------------------------------------------------------*/
        
    public static void pos_prop ( double t0,
                                    double a0[],
                                    double t,
                                    double a[])
    {
        
        /* Auxiliary variables */
        double  tau, alpha0, delta0, par0, pma0, pmd0, zeta0 ;
        double  ca0, sa0, cd0, sd0, xy, alpha, delta, par, pma, pmd, zeta ;
        double  w, f, f2, f3, tau2, pm02 ;
        double r0[] = new double[3], p0[] = new double[3], q0[] = new double[3], pmv0[] = new double[3] ;
        double  pmv[] = new double[3], p[] = new double[3], q[] = new double[3], r[] = new double[3] ;
        
        /* Convert input data to internal units (rad, year) */
        tau    = t - t0         ;
        alpha0 = a0[0] * RDPDEG ;
        delta0 = a0[1] * RDPDEG ;
        par0   = a0[2] * RDPMAS ;
        pma0   = a0[3] * RDPMAS ;
        pmd0   = a0[4] * RDPMAS ;
        zeta0  = a0[5] * RDPMAS ;
        
        /* Calculate normal triad [p0 q0 r0] at t0; r0 is
        also the unit vector to the star at epoch t0 */
        ca0 = Math.cos ( alpha0 ) ;
        sa0 = Math.sin ( alpha0 ) ;
        cd0 = Math.cos ( delta0 ) ;
        sd0 = Math.sin ( delta0 ) ;
        
        p0[0] = - sa0  ;
        p0[1] =   ca0  ;
        p0[2] =   ZERO ;
        
        q0[0] = - sd0 * ca0 ;
        q0[1] = - sd0 * sa0 ;
        q0[2] =   cd0       ;
        
        r0[0] = cd0 * ca0 ;
        r0[1] = cd0 * sa0 ;
        r0[2] = sd0       ;
        
        /* Proper motion vector */
        pmv0[0] = p0[0] * pma0 + q0[0] * pmd0 ;
        pmv0[1] = p0[1] * pma0 + q0[1] * pmd0 ;
        pmv0[2] = p0[2] * pma0 + q0[2] * pmd0 ;
        
        /* Various auxiliary quantities */
        tau2 = tau * tau ;
        pm02 = pma0 * pma0 + pmd0 * pmd0 ;
        w    = ONE + zeta0 * tau ;
        f2   = ONE / ( ONE + TWO * zeta0 * tau + (pm02 + zeta0*zeta0) * tau2 ) ;
        f    = Math.sqrt ( f2 ) ;
        f3   = f2 * f ;
        
        /* The position vector and parallax at t */
        r[0] = ( r0[0] * w + pmv0[0] * tau ) * f ;
        r[1] = ( r0[1] * w + pmv0[1] * tau ) * f ;
        r[2] = ( r0[2] * w + pmv0[2] * tau ) * f ;
        
        par = par0 * f ;
        
        /* The proper motion vector and normalised radial velocity at t */
        pmv[0] = ( pmv0[0] * w - r0[0] * pm02 * tau ) * f3 ;
        pmv[1] = ( pmv0[1] * w - r0[1] * pm02 * tau ) * f3 ;
        pmv[2] = ( pmv0[2] * w - r0[2] * pm02 * tau ) * f3 ;
        
        zeta = ( zeta0 + (pm02 + zeta0 * zeta0) * tau ) * f2 ;
        
        /* The normal triad [p q r] at t; if r is very
        close to the pole, select p towards RA=90 deg */
        xy = Math.sqrt(r[0] * r[0] + r[1] * r[1]) ;
        if ( xy < EPS )
        {
         p[0] = ZERO ;
         p[1] = ONE  ;
         p[2] = ZERO ;
        }
        else
        {
         p[0] = -r[1] / xy ;
         p[1] =  r[0] / xy ;
         p[2] =  ZERO      ;
        }
        
        q[0] = r[1] * p[2] - r[2] * p[1] ;
        q[1] = r[2] * p[0] - r[0] * p[2] ;
        q[2] = r[0] * p[1] - r[1] * p[0] ;
        
        /* Convert parameters at t to external units */
        alpha = Math.atan2 ( -p[0] , p[1] ) ;
        if ( alpha < ZERO )
        alpha = alpha + TWO * PI ;
        delta = Math.atan2 ( r[2] , xy ) ;
        pma = scalar ( p , pmv ) ;
        pmd = scalar ( q , pmv ) ;
        
        a[0] = alpha / RDPDEG ;
        a[1] = delta / RDPDEG ;
        a[2] = par   / RDPMAS ;
        a[3] = pma   / RDPMAS ;
        a[4] = pmd   / RDPMAS ;
        a[5] = zeta  / RDPMAS ;

    }
    
}
