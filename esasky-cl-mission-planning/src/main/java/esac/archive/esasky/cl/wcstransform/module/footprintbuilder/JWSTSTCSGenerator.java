//package esac.archive.esasky.cl.wcstransform.module.footprintbuilder;
//
//import java.math.BigDecimal;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Vector;
//
//import esac.archive.esasky.cl.wcstransform.module.utility.Constants.Detectors;
//import esac.archive.esasky.cl.wcstransform.module.utility.Constants.Instrument;
//import esac.archive.esasky.cl.wcstransform.module.utility.Constants.PlanningMission;
//
///**
// * @author Fabrizio Giordano Copyright (c) 2016 - European Space Agency
// */
//
//public class JWSTSTCSGenerator extends STCSAbstractGenerator {
//
//    public JWSTSTCSGenerator(String mission) {
//        super.mission = mission;
//        setIntruments();
//    }
//
//    private void setIntruments() {
//        super.instruments = new ArrayList<String>();
//        super.instruments.add(Instrument.NIRISS.getInstrumentName());
//        super.instruments.add(Instrument.NIRSPEC.getInstrumentName());
//        super.instruments.add(Instrument.NIRCAM.getInstrumentName());
//        super.instruments.add(Instrument.MIRI.getInstrumentName());
//        super.instruments.add(Instrument.FGS.getInstrumentName());
//    }
//
//    private Vector<double[]> FGSPoly() {
//
//        Vector<double[]> pixels = new Vector<double[]>();
//
//        // FGS1 FULL Vertices Telescope Frame
//        double[] p1nca = new double[] { (280.66), (-771.81) };
//        double[] p2nca = new double[] { (138.36), (-769.86) };
//        double[] p3nca = new double[] { (136.62), (-626.68) };
//        double[] p4nca = new double[] { (275.96), (-627.03) };
//
//        pixels.addElement(p1nca);
//        pixels.addElement(p2nca);
//        pixels.addElement(p3nca);
//        pixels.addElement(p4nca);
//
//        // FGS2 FULL vertices in the Telescope Frame V2V3
//        double[] p1ncb = new double[] { (94.31), (-769.39) };
//        double[] p2ncb = new double[] { (-46.78), (-771.74) };
//        double[] p3ncb = new double[] { (-44.86), (-627.78) };
//        double[] p4ncb = new double[] { (93.43), (-626.34) };
//
//        pixels.addElement(p1ncb);
//        pixels.addElement(p2ncb);
//        pixels.addElement(p3ncb);
//        pixels.addElement(p4ncb);
//
//        return pixels;
//
//    }
//
//    private Vector<double[]> NIRISSPoly() {
//        Vector<double[]> pixels = new Vector<double[]>();
//
//        // NIRISS_CEN Telescope Vertices
//        double[] p1n = new double[] { (-222.43), (-764.48) };
//        double[] p2n = new double[] { (-356.38), (-765.25) };
//        double[] p3n = new double[] { (-357.71), (-630.26) };
//        double[] p4n = new double[] { (-223.78), (-629.60) };
//
//        pixels.addElement(p1n);
//        pixels.addElement(p2n);
//        pixels.addElement(p3n);
//        pixels.addElement(p4n);
//
//        return pixels;
//
//    }
//
//    private Vector<double[]> NIRSPECPoly() {
//
//        Vector<double[]> pixels = new Vector<double[]>();
//        //
////        NIRSpec MOS                    NRS_FULL_MSA – position at center of the four quadrants
////        NIRSpec IFU                      NRS_FULL_IFU -- position of the NIRSpec IFU
////        NIRSpec Slit S200A1         NRS_S200A1_SLIT -- position of slit S200A1
////        NIRSpec Slit S200A2         NRS_S200A2_SLIT -- position of slit S200A2
////        NIRSpec Slit S200B1         NRS_S200B1_SLIT -- position of slit S200B1
////        NIRSpec Slit S400A1         NRS_S400A1_SLIT --position of slit S400A1
////        NIRSpec Slit S1600A1       NRS_S1600A1_SLIT -- position of slit S1600A1
//
//        // Manual Vertices from siafVisTool:
//        // NRS_FULL_MSA
//        double[] v1nrsfull_msafull = new double[] { 385.427, -274.418 };
//        double[] v2nrsfull_msafull = new double[] { 222.368, -419.221 };
//        double[] v3nrsfull_msafull = new double[] { 368.847, -586.342 };
//        double[] v4nrsfull_msafull = new double[] { 535.060, -438.813 };
//
//        pixels.addElement(v1nrsfull_msafull);
//        pixels.addElement(v2nrsfull_msafull);
//        pixels.addElement(v3nrsfull_msafull);
//        pixels.addElement(v4nrsfull_msafull);
//        
//        // NRS_FULL_IFU
//        double[] v1nrsfull_nrsfullifu = new double[] { 300.429, -495.447 };
//        double[] v2nrsfull_nrsfullifu = new double[] { 297.897, -497.785 };
//        double[] v3nrsfull_nrsfullifu = new double[] { 300.152, -500.390 };
//        double[] v4nrsfull_nrsfullifu = new double[] { 302.599, -498.124 };
//
//        pixels.addElement(v1nrsfull_nrsfullifu);
//        pixels.addElement(v2nrsfull_nrsfullifu);
//        pixels.addElement(v3nrsfull_nrsfullifu);
//        pixels.addElement(v4nrsfull_nrsfullifu);
//        
//        // NRS_S200A1_SLIT
//        double[] v1nrsfull_s200a1slit = new double[] { 329.797, -478.019 };
//        double[] v2nrsfull_s200a1slit = new double[] { 329.639, -478.156 };
//        double[] v3nrsfull_s200a1slit = new double[] { 331.739, -480.582};
//        double[] v4nrsfull_s200a1slit = new double[] { 331.928, -480.418 };
//
//        pixels.addElement(v1nrsfull_s200a1slit);
//        pixels.addElement(v2nrsfull_s200a1slit);
//        pixels.addElement(v3nrsfull_s200a1slit);
//        pixels.addElement(v4nrsfull_s200a1slit);
//        
//        // NRS_S200A2_SLIT
//        double[] v1nrsfull_s200a2slit = new double[] { 312.663, -488.275 };
//        double[] v2nrsfull_s200a2slit = new double[] { 312.466, -488.556 };
//        double[] v3nrsfull_s200a2slit = new double[] { 314.538, -490.951 };
//        double[] v4nrsfull_s200a2slit = new double[] { 314.799, -490.615 };
//
//        pixels.addElement(v1nrsfull_s200a2slit);
//        pixels.addElement(v2nrsfull_s200a2slit);
//        pixels.addElement(v3nrsfull_s200a2slit);
//        pixels.addElement(v4nrsfull_s200a2slit);
//        
//        // NRS_S200B1_SLIT
//        double[] v1nrsfull_s200b1slit = new double[] { 437.930, -363.434 };
//        double[] v2nrsfull_s200b1slit = new double[] { 437.678, -363.652 };
//        double[] v3nrsfull_s200b1slit = new double[] { 439.868, -366.055 };
//        double[] v4nrsfull_s200b1slit = new double[] { 440.183, -365.782 };
//
//        pixels.addElement(v1nrsfull_s200b1slit);
//        pixels.addElement(v2nrsfull_s200b1slit);
//        pixels.addElement(v3nrsfull_s200b1slit);
//        pixels.addElement(v4nrsfull_s200b1slit);
//        
//        // NRS_S400A1_SLIT
//        double[] v1nrsfull_s400a1slit = new double[] { 319.508,-476.510 };
//        double[] v2nrsfull_s400a1slit = new double[] { 319.126,-476.896 };
//        double[] v3nrsfull_s400a1slit = new double[] { 321.525,-479.668 };
//        double[] v4nrsfull_s400a1slit = new double[] { 321.935,-479.316 };
//
//        pixels.addElement(v1nrsfull_s400a1slit);
//        pixels.addElement(v2nrsfull_s400a1slit);
//        pixels.addElement(v3nrsfull_s400a1slit);
//        pixels.addElement(v4nrsfull_s400a1slit);
//        
////        // NRS_S800A1_SLIT
////        double[] v1nrsfull_s800a1slit = new double[] { };
////        double[] v2nrsfull_s800a1slit = new double[] { };
////        double[] v3nrsfull_s800a1slit = new double[] { };
////        double[] v4nrsfull_s800a1slit = new double[] { };
////
////        pixels.addElement(v1nrsfull_s800a1slit);
////        pixels.addElement(v2nrsfull_s800a1slit);
////        pixels.addElement(v3nrsfull_s800a1slit);
////        pixels.addElement(v4nrsfull_s800a1slit);
//        
//        // NRS_S1600A1_SLIT
//        double[] v1nrsfull_s1600slit = new double[] { 320.246, -472.741 };
//        double[] v2nrsfull_s1600slit = new double[] { 319.036, -473.788 };
//        double[] v3nrsfull_s1600slit = new double[] { 320.084, -474.997 };
//        double[] v4nrsfull_s1600slit = new double[] { 321.344, -473.907 };
//
//        pixels.addElement(v1nrsfull_s1600slit);
//        pixels.addElement(v2nrsfull_s1600slit);
//        pixels.addElement(v3nrsfull_s1600slit);
//        pixels.addElement(v4nrsfull_s1600slit);
//        
//
//        // NRS_FULL_MSA1
//        // Manual Vertices from siafVisTool:
//        double[] v1nrsfull_msa1 = new double[] { 471.603, -369.741 };
//        double[] v2nrsfull_msa1 = new double[] { 398.929, -435.396 };
//        double[] v3nrsfull_msa1 = new double[] { 460.496, -503.347 };
//        double[] v4nrsfull_msa1 = new double[] { 534.606, -438.288 };
//
//        pixels.addElement(v1nrsfull_msa1);
//        pixels.addElement(v2nrsfull_msa1);
//        pixels.addElement(v3nrsfull_msa1);
//        pixels.addElement(v4nrsfull_msa1);
//
//        // NRS_FULL_MSA2
//        double[] v1nrsfull_msa2 = new double[] { 446.082, -341.316 };
//        double[] v2nrsfull_msa2 = new double[] { 385.881, -274.943 };
//        double[] v3nrsfull_msa2 = new double[] { 313.803, -339.164 };
//        double[] v4nrsfull_msa2 = new double[] { 372.954, -406.446 };
//
//        pixels.addElement(v1nrsfull_msa2);
//        pixels.addElement(v2nrsfull_msa2);
//        pixels.addElement(v3nrsfull_msa2);
//        pixels.addElement(v4nrsfull_msa2);
//
//        // NRS_FULL_MSA3
//        double[] v1nrsfull_msa3 = new double[] { 379.572, -451.232 };
//        double[] v2nrsfull_msa3 = new double[] { 307.878, -516.957 };
//        double[] v3nrsfull_msa3 = new double[] { 367.867, -586.271 };
//        double[] v4nrsfull_msa3 = new double[] { 441.976, -521.212 };
//
//        pixels.addElement(v1nrsfull_msa3);
//        pixels.addElement(v2nrsfull_msa3);
//        pixels.addElement(v3nrsfull_msa3);
//        pixels.addElement(v4nrsfull_msa3);
//
//        // NRS_FULL_MSA4
//        double[] v1nrsfull_msa4 = new double[] { 295.354, -356.050 };
//        double[] v2nrsfull_msa4 = new double[] { 223.802, -419.816 };
//        double[] v3nrsfull_msa4 = new double[] { 282.498, -486.573 };
//        double[] v4nrsfull_msa4 = new double[] { 354.576, -422.352 };
//
//        pixels.addElement(v1nrsfull_msa4);
//        pixels.addElement(v2nrsfull_msa4);
//        pixels.addElement(v3nrsfull_msa4);
//        pixels.addElement(v4nrsfull_msa4);
//
//        return pixels;
//
//    }
//
//    private Vector<double[]> NIRCAMPoly() {
//
//        Vector<double[]> pixels = new Vector<double[]>();
//        
//        // NIRCA1_FULL Telescope Vertices
//        double[] v1nirca1 = new double[] { (153.164), (-559.273) };
//        double[] v2nirca1 = new double[] { (88.896), (-559.884) };
//        double[] v3nirca1 = new double[] { (88.685), (-495.585) };
//        double[] v4nirca1 = new double[] { (152.075), (-495.147) };
//
//        pixels.addElement(v1nirca1);
//        pixels.addElement(v2nirca1);
//        pixels.addElement(v3nirca1);
//        pixels.addElement(v4nirca1);
//
//        // NIRCA2_FULL Telescope Vertices
//        double[] v1nirca2 = new double[] { (151.952), (-491.114) };
//        double[] v2nirca2 = new double[] { (88.563), (-491.492) };
//        double[] v3nirca2 = new double[] { (88.682), (-428.166) };
//        double[] v4nirca2 = new double[] { (151.375), (-427.953) };
//
//        pixels.addElement(v1nirca2);
//        pixels.addElement(v2nirca2);
//        pixels.addElement(v3nirca2);
//        pixels.addElement(v4nirca2);
//
//        // NIRCA3_FULL Telescope Vertices
//        double[] v1nirca3 = new double[] { (84.054), (-560.025) };
//        double[] v2nirca3 = new double[] { (19.489), (-560.223) };
//        double[] v3nirca3 = new double[] { (20.152), (-495.586) };
//        double[] v4nirca3 = new double[] { (83.827), (-495.729) };
//
//        pixels.addElement(v1nirca3);
//        pixels.addElement(v2nirca3);
//        pixels.addElement(v3nirca3);
//        pixels.addElement(v4nirca3);
//
//        // NIRCA4_FULL Telescope Vertices
//        double[] v1nirca4 = new double[] { (84.003), (-491.458) };
//        double[] v2nirca4 = new double[] { (20.389), (-491.588) };
//        double[] v3nirca4 = new double[] { (20.800), (-427.999) };
//        double[] v4nirca4 = new double[] { (83.714), (-428.198) };
//
//        pixels.addElement(v1nirca4);
//        pixels.addElement(v2nirca4);
//        pixels.addElement(v3nirca4);
//        pixels.addElement(v4nirca4);
//
//        // NIRCA5_FULL_OSS Telescope Vertices
//        double[] v1nirca5 = new double[] { (151.452), (-557.792) };
//        double[] v2nirca5 = new double[] { (20.882), (-558.596) };
//        double[] v3nirca5 = new double[] { (22.232), (-428.456) };
//        double[] v4nirca5 = new double[] { (149.725), (-428.629) };
//
//        pixels.addElement(v1nirca5);
//        pixels.addElement(v2nirca5);
//        pixels.addElement(v3nirca5);
//        pixels.addElement(v4nirca5);
//
//        // NIRCB1_FULL Telescope Vertices
//        double[] v1nircb1 = new double[] { (-89.587), (-489.579) };
//        double[] v2nircb1 = new double[] { (-152.822), (-489.066) };
//        double[] v3nircb1 = new double[] { (-152.070), (-426.004) };
//        double[] v4nircb1 = new double[] { (-89.522), (-426.354) };
//
//        pixels.addElement(v1nircb1);
//        pixels.addElement(v2nircb1);
//        pixels.addElement(v3nircb1);
//        pixels.addElement(v4nircb1);
//
//        // NIRCB2_FULL Telescope Vertices
//        double[] v1nircb2 = new double[] { (-89.554), (-558.092) };
//        double[] v2nircb2 = new double[] { (-153.738), (-557.135) };
//        double[] v3nircb2 = new double[] { (-152.368), (-493.079) };
//        double[] v4nircb2 = new double[] { (-89.053), (-493.855) };
//
//        pixels.addElement(v1nircb2);
//        pixels.addElement(v2nircb2);
//        pixels.addElement(v3nircb2);
//        pixels.addElement(v4nircb2);
//
//        // NIRCB3_FULL Telescope Vertices
//        double[] v1nircb3 = new double[] { (-21.049), (-489.319) };
//        double[] v2nircb3 = new double[] { (-84.567), (-489.589) };
//        double[] v3nircb3 = new double[] { (-84.751), (-426.397) };
//        double[] v4nircb3 = new double[] { (-21.931), (-425.811) };
//
//        pixels.addElement(v1nircb3);
//        pixels.addElement(v2nircb3);
//        pixels.addElement(v3nircb3);
//        pixels.addElement(v4nircb3);
//
//        // NIRCB4_FULL Telescope Vertices
//        double[] v1nircb4 = new double[] { (-20.319), (-558.079) };
//        double[] v2nircb4 = new double[] { (-84.816), (-557.988) };
//        double[] v3nircb4 = new double[] { (-84.769), (-493.710) };
//        double[] v4nircb4 = new double[] { (-21.158), (-493.468) };
//
//        pixels.addElement(v1nircb4);
//        pixels.addElement(v2nircb4);
//        pixels.addElement(v3nircb4);
//        pixels.addElement(v4nircb4);
//
//        // NIRCB5_FULL Telescope Vertices
//        double[] v1nircb5 = new double[] { (-23.975), (-556.865) };
//        double[] v2nircb5 = new double[] { (-154.748), (-556.159) };
//        double[] v3nircb5 = new double[] { (-153.247), (-426.749) };
//        double[] v4nircb5 = new double[] { (-25.543), (-426.523) };
//
//        pixels.addElement(v1nircb5);
//        pixels.addElement(v2nircb5);
//        pixels.addElement(v3nircb5);
//        pixels.addElement(v4nircb5);
//
//        // NIRCA2_MASK210R Telescope Vertices
//        double[] v1nirca2_mask210r = new double[] { (137.109), (-415.035) };
//        double[] v2nirca2_mask210r = new double[] { (117.435), (-415.152) };
//        double[] v3nirca2_mask210r = new double[] { (117.386), (-395.445) };
//        double[] v4nirca2_mask210r = new double[] { (136.996), (-395.340) };
//
//        pixels.addElement(v1nirca2_mask210r);
//        pixels.addElement(v2nirca2_mask210r);
//        pixels.addElement(v3nirca2_mask210r);
//        pixels.addElement(v4nirca2_mask210r);
//
//        // NIRCA5_MASK335R Telescope Vertices
//        double[] v1nirca5_mask335r = new double[] { (117.530), (-415.483) };
//        double[] v2nirca5_mask335r = new double[] { (97.555), (-415.578) };
//        double[] v3nirca5_mask335r = new double[] { (97.521), (-395.537) };
//        double[] v4nirca5_mask335r = new double[] { (117.434), (-395.457) };
//
//        pixels.addElement(v1nirca5_mask335r);
//        pixels.addElement(v2nirca5_mask335r);
//        pixels.addElement(v3nirca5_mask335r);
//        pixels.addElement(v4nirca5_mask335r);
//
//        // NIRCA5_MASK430R Telescope Vertices
//        double[] v1nirca5_mask430r = new double[] { (97.242), (-415.203) };
//        double[] v2nirca5_mask430r = new double[] { (77.255), (-415.226) };
//        double[] v3nirca5_mask430r = new double[] { (77.284), (-395.163) };
//        double[] v4nirca5_mask430r = new double[] { (97.209), (-395.163) };
//
//        pixels.addElement(v1nirca5_mask430r);
//        pixels.addElement(v2nirca5_mask430r);
//        pixels.addElement(v3nirca5_mask430r);
//        pixels.addElement(v4nirca5_mask430r);
//
//        // NIRCA4_MASKSWB Telescope Vertices
//        double[] v1nirca4_maskswb = new double[] { (77.471), (-414.510) };
//        double[] v2nirca4_maskswb = new double[] { (57.769), (-414.552) };
//        double[] v3nirca4_maskswb = new double[] { (57.774), (-394.803) };
//        double[] v4nirca4_maskswb = new double[] { (77.471), (-394.790) };
//
//        pixels.addElement(v1nirca4_maskswb);
//        pixels.addElement(v2nirca4_maskswb);
//        pixels.addElement(v3nirca4_maskswb);
//        pixels.addElement(v4nirca4_maskswb);
//
//        // NIRCA5_MASKLWB Telescope Vertices
//        double[] v1nirca5_masklwb = new double[] { (57.176), (-414.548) };
//        double[] v2nirca5_masklwb = new double[] { (37.109), (-414.426) };
//        double[] v3nirca5_masklwb = new double[] { (37.263), (-394.301) };
//        double[] v4nirca5_masklwb = new double[] { (57.268), (-394.458) };
//
//        pixels.addElement(v1nirca5_masklwb);
//        pixels.addElement(v2nirca5_masklwb);
//        pixels.addElement(v3nirca5_masklwb);
//        pixels.addElement(v4nirca5_masklwb);
//
//        // NIRCB1_MASK210R Telescope Vertices
//        double[] v1nircb1_mask210r = new double[] { (-117.411), (-411.654) };
//        double[] v2nircb1_mask210r = new double[] { (-137.033), (-411.497) };
//        double[] v3nircb1_mask210r = new double[] { (-136.870), (-391.837) };
//        double[] v4nircb1_mask210r = new double[] { (-117.311), (-391.981) };
//
//        pixels.addElement(v1nircb1_mask210r);
//        pixels.addElement(v2nircb1_mask210r);
//        pixels.addElement(v3nircb1_mask210r);
//        pixels.addElement(v4nircb1_mask210r);
//
//        // NIRCB5_MASK335R Telescope Vertices
//        double[] v1nircb5_mask335r = new double[] { (-97.451), (-412.103) };
//        double[] v2nircb5_mask335r = new double[] { (-117.458), (-412.030) };
//        double[] v3nircb5_mask335r = new double[] { (-117.412), (-391.966) };
//        double[] v4nircb5_mask335r = new double[] { (-97.466), (-392.023) };
//
//        pixels.addElement(v1nircb5_mask335r);
//        pixels.addElement(v2nircb5_mask335r);
//        pixels.addElement(v3nircb5_mask335r);
//        pixels.addElement(v4nircb5_mask335r);
//
//        // NIRCB5_MASK430R Telescope Vertices
//        double[] v1nircb5_mask430r = new double[] { (-77.180), (-412.228) };
//        double[] v2nircb5_mask430r = new double[] { (-97.200), (-412.229) };
//        double[] v3nircb5_mask430r = new double[] { (-97.216), (-392.149) };
//        double[] v4nircb5_mask430r = new double[] { (-77.257), (-392.126) };
//
//        pixels.addElement(v1nircb5_mask430r);
//        pixels.addElement(v2nircb5_mask430r);
//        pixels.addElement(v3nircb5_mask430r);
//        pixels.addElement(v4nircb5_mask430r);
//
//        // NIRCB3_MASKSWB Telescope Vertices
//        double[] v1nircb3_maskswb = new double[] { (-37.315), (-411.699) };
//        double[] v2nircb3_maskswb = new double[] { (-57.034), (-411.857) };
//        double[] v3nircb3_maskswb = new double[] { (-57.191), (-392.130) };
//        double[] v4nircb3_maskswb = new double[] { (-37.536), (-391.940) };
//
//        pixels.addElement(v1nircb3_maskswb);
//        pixels.addElement(v2nircb3_maskswb);
//        pixels.addElement(v3nircb3_maskswb);
//        pixels.addElement(v4nircb3_maskswb);
//
//        // NIRCB5_MASKLWB Vertices
//        double[] v1nircb5_masklwb = new double[] { (-57.189), (-412.343) };
//        double[] v2nircb5_masklwb = new double[] { (-77.242), (-412.417) };
//        double[] v3nircb5_masklwb = new double[] { (-77.319), (-392.314) };
//        double[] v4nircb5_masklwb = new double[] { (-57.327), (-392.213) };
//
//        pixels.addElement(v1nircb5_masklwb);
//        pixels.addElement(v2nircb5_masklwb);
//        pixels.addElement(v3nircb5_masklwb);
//        pixels.addElement(v4nircb5_masklwb);
//
//        return pixels;
//    }
//
//    public Vector<double[]> MIRIPoly() {
//
//        Vector<double[]> pixels = new Vector<double[]>();
//
//        // MIRIM_FULL Telescope Vertices
//        double[] v1mirim_full_oss = new double[] { (-381.816), (-435.877) };
//        double[] v2mirim_full_oss = new double[] { (-495.802), (-426.597) };
//        double[] v3mirim_full_oss = new double[] { (-485.644), (-314.115) };
//        double[] v4mirim_full_oss = new double[] { (-372.943), (-323.492) };
//
//        pixels.addElement(v1mirim_full_oss);
//        pixels.addElement(v2mirim_full_oss);
//        pixels.addElement(v3mirim_full_oss);
//        pixels.addElement(v4mirim_full_oss);
//
////        // MIRIM_SUB256
////        double[] v1mirim_sub256 = new double[] {(-426.827),(-426.952) };
////        double[] v2mirim_sub256 = new double[] {(-455.142),(-424.644) };
////        double[] v3mirim_sub256 = new double[] {(-452.745),(-396.559) };
////        double[] v4mirim_sub256 = new double[] {(-424.486),(-426.952) };
////
////        pixels.addElement(v1mirim_sub256);
////        pixels.addElement(v2mirim_sub256);
////        pixels.addElement(v3mirim_sub256);
////        pixels.addElement(v4mirim_sub256);
////        
////
////        // MIRIM_SUB128
////        double[] v1mirim_sub128 = new double[] {(-374.020),(-338.400)};
////        double[] v2mirim_sub128 = new double[] {(-388.900),(-337.036)};
////        double[] v3mirim_sub128 = new double[] {(-387.790),(-322.989)};
////        double[] v4mirim_sub128 = new double[] {(-372.933),(-324.351)};
////
////        pixels.addElement(v1mirim_sub128);
////        pixels.addElement(v2mirim_sub128);
////        pixels.addElement(v3mirim_sub128);
////        pixels.addElement(v4mirim_sub128);
////        
////        // MIRIM_SUB64
////        double[] v1mirim_sub64 = new double[] {(-374.974),(-350.523)};
////        double[] v2mirim_sub64 = new double[] {(-382.864),(-349.800)};
////        double[] v3mirim_sub64 = new double[] {(-382.316),(-342.747)};
////        double[] v4mirim_sub64 = new double[] {(-374.432),(-343.472)};
////
////        pixels.addElement(v1mirim_sub64);
////        pixels.addElement(v2mirim_sub64);
////        pixels.addElement(v3mirim_sub64);
////        pixels.addElement(v4mirim_sub64);
//        
//         // MIRIM_SLIT.
////         double[] v1mirim_slit = new double[] { (-412.414), (-400.643) };
////         double[] v2mirim_slit = new double[] { (-417.709), (-400.202) };
////         double[] v3mirim_slit = new double[] { (-417.672), (-399.760) };
////         double[] v4mirim_slit = new double[] { (-412.378), (-400.201) };
////        
////         pixels.addElement(v1mirim_slit);
////         pixels.addElement(v2mirim_slit);
////         pixels.addElement(v3mirim_slit);
////         pixels.addElement(v4mirim_slit);
//        
//         // MIRIM_CHANNEL1A.
//         double[] v1mirim_channel1a = new double[] { (-498.797), (-316.866) };
//         double[] v2mirim_channel1a = new double[] { (-500.269), (-324.458) };
//         double[] v3mirim_channel1a = new double[] { (-507.500), (-323.400) };
//         double[] v4mirim_channel1a = new double[] { (-506.189), (-315.795) };
//        
//         pixels.addElement(v1mirim_channel1a);
//         pixels.addElement(v2mirim_channel1a);
//         pixels.addElement(v3mirim_channel1a);
//         pixels.addElement(v4mirim_channel1a);
//         
//        // MIRIM_ILLUM.
//        double[] v1mirim_illum = new double[] { (-421.423), (-432.819) };
//        double[] v2mirim_illum = new double[] { (-495.251), (-426.646) };
//        double[] v3mirim_illum = new double[] { (-485.096), (-314.155) };
//        double[] v4mirim_illum = new double[] { (-412.127), (-319.970) };
//
//        pixels.addElement(v1mirim_illum);
//        pixels.addElement(v2mirim_illum);
//        pixels.addElement(v3mirim_illum);
//        pixels.addElement(v4mirim_illum);
//
//        // MIRIM_FP1MIMF
//        double[] v1mirim_fp1mimf = new double[] { (-381.816), (-435.877) };
//        double[] v2mirim_fp1mimf = new double[] { (-495.802), (-426.597) };
//        double[] v3mirim_fp1mimf = new double[] { (-485.644), (-314.115) };
//        double[] v4mirim_fp1mimf = new double[] { (-372.943), (-323.492) };
//
//        pixels.addElement(v1mirim_fp1mimf);
//        pixels.addElement(v2mirim_fp1mimf);
//        pixels.addElement(v3mirim_fp1mimf);
//        pixels.addElement(v4mirim_fp1mimf);
//
//        // // MIRIM_SLITLEPRISM.
//        // double[] v1mirim_slitleprism = new double[] { (-377.104), (-378.157) };
//        // double[] v2mirim_slitleprism = new double[] { (-385.020), (-377.452) };
//        // double[] v3mirim_slitleprism = new double[] { (-381.450), (-331.536) };
//        // double[] v4mirim_slitleprism = new double[] { (-373.576), (-332.261) };
//        //
//        // pixels.addElement(v1mirim_slitleprism);
//        // pixels.addElement(v2mirim_slitleprism);
//        // pixels.addElement(v3mirim_slitleprism);
//        // pixels.addElement(v4mirim_slitleprism);
//        //
//        // // MIRIM_SLIT.
//        // double[] v1mirim_slit = new double[] { (-412.414), (-400.643) };
//        // double[] v2mirim_slit = new double[] { (-417.709), (-400.202) };
//        // double[] v3mirim_slit = new double[] { (-417.672), (-399.760) };
//        // double[] v4mirim_slit = new double[] { (-412.378), (-400.201) };
//        //
//        // pixels.addElement(v1mirim_slit);
//        // pixels.addElement(v2mirim_slit);
//        // pixels.addElement(v3mirim_slit);
//        // pixels.addElement(v4mirim_slit);
//        //
//        // // MIRIM_BRIGHTSKY
//        // double[] v1mirim_brightsky = new double[] { (-431.694), (-426.557) };
//        // double[] v2mirim_brightsky = new double[] { (-488.305), (-421.860) };
//        // double[] v3mirim_brightsky = new double[] { (-483.312), (-365.602) };
//        // double[] v4mirim_brightsky = new double[] { (-426.967), (-370.042) };
//        //
//        // pixels.addElement(v1mirim_brightsky);
//        // pixels.addElement(v2mirim_brightsky);
//        // pixels.addElement(v3mirim_brightsky);
//        // pixels.addElement(v4mirim_brightsky);
//
//        // MIRIM_MASK1065 V2V3 Vertices
//        double[] v1mirim_mask1065 = new double[] { (-381.612), (-433.992) };
//        double[] v2mirim_mask1065 = new double[] { (-413.377), (-431.538) };
//        double[] v3mirim_mask1065 = new double[] { (-411.350), (-407.094) };
//        double[] v4mirim_mask1065 = new double[] { (-379.591), (-409.734) };
//
//        pixels.addElement(v1mirim_mask1065);
//        pixels.addElement(v2mirim_mask1065);
//        pixels.addElement(v3mirim_mask1065);
//        pixels.addElement(v4mirim_mask1065);
//
//        // MIRIM_MASK1140 Vertices (Telescope V2, V3)
//        double[] v1mirim_mask1140 = new double[] { (-379.595), (-409.491) };
//        double[] v2mirim_mask1140 = new double[] { (-411.354), (-406.850) };
//        double[] v3mirim_mask1140 = new double[] { (-409.336), (-382.048) };
//        double[] v4mirim_mask1140 = new double[] { (-377.650), (-384.807) };
//
//        pixels.addElement(v1mirim_mask1140);
//        pixels.addElement(v2mirim_mask1140);
//        pixels.addElement(v3mirim_mask1140);
//        pixels.addElement(v4mirim_mask1140);
//
//        // MIRIM_MASK1550 V2V3 Vertices
//        double[] v1mirim_mask1550 = new double[] { (-377.668), (-385.022) };
//        double[] v2mirim_mask1550 = new double[] { (-409.355), (-382.264) };
//        double[] v3mirim_mask1550 = new double[] { (-407.346), (-357.422) };
//        double[] v4mirim_mask1550 = new double[] { (-375.759), (-360.246) };
//
//        pixels.addElement(v1mirim_mask1550);
//        pixels.addElement(v2mirim_mask1550);
//        pixels.addElement(v3mirim_mask1550);
//        pixels.addElement(v4mirim_mask1550);
//
//        // MIRIM_MASKLYOT Telescope Vertices
//        double[] v1mirim_masklyot = new double[] { (-375.514), (-357.363) };
//        double[] v2mirim_masklyot = new double[] { (-410.596), (-354.230) };
//        double[] v3mirim_masklyot = new double[] { (-407.870), (-320.770) };
//        double[] v4mirim_masklyot = new double[] { (-372.938), (-323.922) };
//
//        pixels.addElement(v1mirim_masklyot);
//        pixels.addElement(v2mirim_masklyot);
//        pixels.addElement(v3mirim_masklyot);
//        pixels.addElement(v4mirim_masklyot);
//
//        // MIRIM_TALYOT_LL Telescope Vertices V2, V3...
//        // double[] v1mirim_talyot_ll = new double[] { (-378.573), (-349.861) };
//        // double[] v2mirim_talyot_ll = new double[] { (-385.586), (-349.221) };
//        // double[] v3mirim_talyot_ll = new double[] { (-385.035), (-342.167) };
//        // double[] v4mirim_talyot_ll = new double[] { (-378.028), (-342.809) };
//        //
//        // pixels.addElement(v1mirim_talyot_ll);
//        // pixels.addElement(v2mirim_talyot_ll);
//        // pixels.addElement(v3mirim_talyot_ll);
//        // pixels.addElement(v4mirim_talyot_ll);
//
//        return pixels;
//
//    }
//
//    // @Override
//    // public Vector<double[]> computeInstrumentPolygon(String instrument,
//    // double rotationDeg, double raDeg, double decDeg, boolean fullFoVMode) {
//    //
//    // Vector<double[]> pixels = new Vector<double[]>();
//    //
//    // if (Instrument.NIRISS.getInstrumentName().equals(instrument)) {
//    // pixels = NIRISSPoly();
//    //
//    //
//    // } else if (Instrument.NIRSPEC.getInstrumentName().equals(instrument)) {
//    // pixels = NIRSPECPoly();
//    //
//    //
//    // } else if (Instrument.NIRCAM.getInstrumentName().equals(instrument)) {
//    //
//    // pixels = NIRCAMPoly();
//    //
//    //
//    // } else if (Instrument.MIRI.getInstrumentName().equals(instrument)) {
//    // pixels = MIRIPoly();
//    //
//    //
//    // } else if (Instrument.FGS.getInstrumentName().equals(instrument)) {
//    // pixels = FGSPoly();
//    //
//    // }
//    //
//    // if (pixels == null) {
//    // return null;
//    // }
//    //
//    //
//    // double[] referencePosV2V3 = selectReferencePosVFrame(instrument, fullFoVMode);
//    //
//    // System.out.println("{RA:" + raDeg + ",Dec:" + decDeg + ", PA: "+ rotationDeg + "{V2="
//    // +referencePosV2V3[0]*3600 + ", V3=" + referencePosV2V3[1]*3600 +"}");
//    //
//    // BigDecimal[][] vToSkyFrameMatrix = JWSTSIAFUtils
//    // .getTelescopeToSkyFrameReferenceMatrix(raDeg, decDeg, rotationDeg, referencePosV2V3[0],
//    // referencePosV2V3[1]);
//    //
//    // Vector<double[]> skycoords = projectPixelsToSkyCoords(pixels,
//    // vToSkyFrameMatrix);
//    //
//    // return skycoords;
//    //
//    // }
//
//    @Override
//    public Map<String, Vector<double[]>> computeInstrumentPolygon(String instrument,
//            String detector, double rotationDeg, double raDeg, double decDeg) {
////
////        Vector<double[]> pixels = new Vector<double[]>();
//
//        Map<String, Vector<double[]>> fullFovPixels = new HashMap<String, Vector<double[]>>();
//        Map<String, Vector<double[]>> fullFovSkyCoords = new HashMap<String, Vector<double[]>>();
//
//        fullFovPixels.put(Instrument.NIRISS.getInstrumentName(), NIRISSPoly());
//        fullFovPixels.put(Instrument.NIRSPEC.getInstrumentName(), NIRSPECPoly());
//        fullFovPixels.put(Instrument.NIRCAM.getInstrumentName(), NIRCAMPoly());
//        fullFovPixels.put(Instrument.MIRI.getInstrumentName(), MIRIPoly());
//        fullFovPixels.put(Instrument.FGS.getInstrumentName(), FGSPoly());
//
//        for (Vector<double[]> pixel : fullFovPixels.values()) {
//            if (pixel.isEmpty() || pixel == null) {
//                return null;
//            }
//        }
//
//        double[] referencePosV2V3 = selectReferencePosVFrame(instrument, detector);
//
//        System.out.println("{ComputeInstrumentPolygon: RA:" + raDeg + ",Dec:" + decDeg + ", PA: " + rotationDeg + "{V2="
//                + referencePosV2V3[0] * 3600 + ", V3=" + referencePosV2V3[1] * 3600 + "}");
//
//        BigDecimal[][] vToSkyFrameMatrix = JWSTSIAFUtils.getTelescopeToSkyFrameReferenceMatrix(
//                raDeg, decDeg, rotationDeg, referencePosV2V3[0], referencePosV2V3[1]);
//
//        for (String inst : fullFovPixels.keySet()) {
//
//            fullFovSkyCoords.put(inst,
//                    projectPixelsToSkyCoords(fullFovPixels.get(inst), vToSkyFrameMatrix));
//        }
//
//        // Vector<double[]> skycoords = projectPixelsToSkyCoords(pixels,
//        // vToSkyFrameMatrix);
//        return fullFovSkyCoords;
//        // return skycoords;
//
//    }
//
//    private Vector<double[]> projectPixelsToSkyCoords(Vector<double[]> pixels,
//            BigDecimal[][] rotationMatrix) {
//
//        Vector<double[]> skycoords = new Vector<double[]>();
//
//        for (double[] pixel : pixels) {
//
//            double[] coords = JWSTSIAFUtils.convertTelescopeToSkyCoords(pixel[0] / 3600.,
//                    pixel[1] / 3600., rotationMatrix);
//
//            skycoords.addElement(coords);
//        }
//
//        return skycoords;
//    }
//
//    @Override
//    public String generateSTCS(Vector<double[]> pixels) {
//        StringBuilder sb = new StringBuilder();
//        for (int k = 0; k < pixels.size(); k++) {
//            if ((k % 4) == 0) {
//                sb.append(" POLYGON J2000");
//            }
//
//            double[] point = pixels.elementAt(k);
//
//            sb.append(" ").append(point[0]).append(" ").append(point[1]);
//        }
//        // System.out.println(sb.toString());
//        return sb.toString();
//    }
//
//    @Override
//    public double[] selectReferencePosVFrame(String instrument, String detector) {
//
//        double[] reference = null;
//
//        if (detector == null) {
//            for (Detectors d : Detectors.values()) {
//
//                if (d.getInstrumentName().equals(instrument) && instrument.equals("FGS")) {
//                    reference = (Detectors.FGS1.getPointReferenceInVFrame());
//                    break;
//                } else if (d.getInstrumentName().equals(instrument) && instrument.equals("NIRISS")) {
//                    reference = Detectors.NIS_CEN.getPointReferenceInVFrame();
//                    break;
//                } else if (d.getInstrumentName().equals(instrument) && instrument.equals("NIRCAM")) {
//                    reference = Detectors.NIRCALL_FULL.getPointReferenceInVFrame();
//                    break;
//                } else if (d.getInstrumentName().equals(instrument) && instrument.equals("NIRSPEC")) {
//                    reference = Detectors.NRS_FULL_MSA.getPointReferenceInVFrame();
//                    break;
//                } else if (d.getInstrumentName().equals(instrument) && instrument.equals("MIRI")) {
//                    reference = Detectors.MIRIM_FULL.getPointReferenceInVFrame();
//                    break;
//                }
//            }
//        } else {
//            for (Detectors d : Detectors.values()) {
//                if (d.getDetectorName().equals(detector) && d.getInstrumentName().equals(instrument)) {
//                    reference = d.getPointReferenceInVFrame();
//                    break;
//                }
//            }
//
//        }
//
//        return new double[] { reference[0] / 3600., reference[1] / 3600. };
//
//    }
//
//    // @Override
//    public Map<Detectors, Vector<double[]>> getDetectorsSkyCoordsForInstrument(double raDeg,
//            double decDeg, double rotationDeg, String instrument, String detector) {
//
//        Map<Detectors, Vector<double[]>> detectorMap = new HashMap<Detectors, Vector<double[]>>();
//
//        List<Detectors> listOfDetectors = Detectors.getDetectorsForInstrument(instrument);
//
//        double[] referencePosV2V3 = selectReferencePosVFrame(instrument, detector);
//
//        System.out.println("{DetectorsSkyCoordsForInstrument: RA:" + raDeg + ",Dec:" + decDeg + ", PA: " + rotationDeg + "{V2="
//                + referencePosV2V3[0] * 3600 + ", V3=" + referencePosV2V3[1] * 3600 + "}");
//
//        BigDecimal[][] vToSkyFrameMatrix = JWSTSIAFUtils.getTelescopeToSkyFrameReferenceMatrix(
//                raDeg, decDeg, rotationDeg, referencePosV2V3[0], referencePosV2V3[1]);
//
//        Vector<double[]> detectorCentersInVFrame = new Vector<double[]>();
//        Vector<double[]> detectorCentersInSkyFrame = new Vector<double[]>();
//
//        for (Detectors d : listOfDetectors) {
//
//            detectorCentersInVFrame = new Vector<double[]>();
//            detectorCentersInSkyFrame = new Vector<double[]>();
//
//            detectorCentersInVFrame.add(d.getPointReferenceInVFrame());
//            detectorCentersInSkyFrame = projectPixelsToSkyCoords(detectorCentersInVFrame,
//                    vToSkyFrameMatrix);
//
//            detectorMap.put(d, detectorCentersInSkyFrame);
//        }
//
//        return detectorMap;
//    }
//
//    // @Override
//    @Override
//    public Map<Detectors, Vector<double[]>> getDetectorsSkyCoordsForFoV(double raDeg,
//            double decDeg, double rotationDeg, String instrument, String detector) {
//
//        Map<Detectors, Vector<double[]>> detectorMap = new HashMap<Detectors, Vector<double[]>>();
//
//        double[] referencePosV2V3 = selectReferencePosVFrame(instrument, detector);
//
//        System.out.println("getDetectorsSkyCoords{RA:" + raDeg + ",Dec:" + decDeg + ", PA: " + rotationDeg + "{V2="
//                + referencePosV2V3[0] * 3600 + ", V3=" + referencePosV2V3[1] * 3600 + "}");
//
//        BigDecimal[][] vToSkyFrameMatrix = JWSTSIAFUtils.getTelescopeToSkyFrameReferenceMatrix(
//                raDeg, decDeg, rotationDeg, referencePosV2V3[0], referencePosV2V3[1]);
//
//        Vector<double[]> detectorCentersInVFrame = new Vector<double[]>();
//        Vector<double[]> detectorCentersInSkyFrame = new Vector<double[]>();
//
//        for (Instrument inst : Instrument.getInstrumentsPerMission(PlanningMission.JWST)) {
//            List<Detectors> listOfDetectors = Detectors.getDetectorsForInstrument(inst
//                    .getInstrumentName());
//            for (Detectors d : listOfDetectors) {
//
//                detectorCentersInVFrame = new Vector<double[]>();
//                detectorCentersInSkyFrame = new Vector<double[]>();
//
//                detectorCentersInVFrame.add(d.getPointReferenceInVFrame());
//                detectorCentersInSkyFrame = projectPixelsToSkyCoords(detectorCentersInVFrame,
//                        vToSkyFrameMatrix);
//
//                detectorMap.put(d, detectorCentersInSkyFrame);
//            }
//        }
//
//        return detectorMap;
//    }
//
//}
