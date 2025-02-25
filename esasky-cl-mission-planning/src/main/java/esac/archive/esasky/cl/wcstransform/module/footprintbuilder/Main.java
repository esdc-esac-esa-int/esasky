/*
ESASky
Copyright (C) 2025 Henrik Norman

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published
by the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

package esac.archive.esasky.cl.wcstransform.module.footprintbuilder;

import java.util.Map;

import esac.archive.esasky.cl.wcstransform.module.utility.SiafDescriptor;
import esac.archive.esasky.cl.wcstransform.module.utility.Constants.Instrument;
import esac.archive.esasky.cl.wcstransform.module.utility.Constants.PlanningMission;
import esac.archive.esasky.ifcs.model.shared.EsaSkyConstants.JWSTInstrument;

/**
 * @author Fabrizio Giordano Copyright (c) 2016 - European Space Agency
 */

public class Main {

    public static void main(String args[]) {
    	new SiafDescriptor("localhost:8080/esasky-sl/");
        double cra = 202.468;
        double cdec = 47.195;
        double rotationAngle = 0.0;


        Map<String,String> stcsPolygon = STCSGeneratorFactory.getSTCSGenerator(
                PlanningMission.JWST.getMissionName()).doAll(
                Instrument.MIRI.getInstrumentName(), JWSTInstrument.MIRIM_FULL.getAperName(),rotationAngle, cra, cdec);
  
        String test = "(4.54309204294124e-05 , 0),(4.45579780323381e-05 , -8.86313289605312e-06),(4.1972697527883e-05 , -1.73856605654166e-05),(3.77744297959914e-05 , -2.52400670488484e-05),(3.21245119111839e-05 , -3.21245119110358e-05),(2.52400670492637e-05 , -3.77744297956895e-05),(1.73856605654329e-05 , -4.19726975282586e-05),(8.86313289606322e-06 , -4.45579780326e-05),(0 , -4.54309204291903e-05),(6.28317644404669 , -4.45579780326e-05),(6.28316792151902 , -4.19726975282586e-05),(6.28316006711254 , -3.77744297956895e-05),(6.28315318266768 , -3.21245119110358e-05),(6.28314753274979 , -2.52400670488484e-05),(6.28314333448206 , -1.73856605654166e-05),(6.28314074920155 , -8.86313289605312e-06),(6.28313987625916 , 0),(6.28314074920155 , 8.86313289606301e-06),(6.28314333448206 , 1.73856605654327e-05),(6.28314753274979 , 2.52400670492636e-05),(6.28315318266768 , 3.21245119111839e-05),(6.28316006711254 , 3.77744297959912e-05),(6.28316792151902 , 4.1972697527883e-05),(6.28317644404669 , 4.45579780323381e-05),(0 , 4.54309204294124e-05),(8.86313289606301e-06 , 4.45579780323381e-05),(1.73856605654327e-05 , 4.1972697527883e-05),(2.52400670492636e-05 , 3.77744297959914e-05),(3.21245119111839e-05 , 3.21245119111841e-05),(3.77744297959912e-05 , 2.52400670492637e-05),(4.1972697527883e-05 , 1.73856605654329e-05),(4.45579780323381e-05 , 8.86313289606327e-06)";
//        String test = "(2.8173804202054 , -1.04736296894416),(2.81252841303921 , -1.0471994516015),(2.80756668444152 , -1.04702234120324),(2.80756509374348 , -1.04702183122496),(2.80756607899811 , -1.04693778572819),(2.807921188198 , -1.04451231923209),(2.80965445731575 , -1.03883138885456),(2.80965527200514 , -1.038831036157),(2.80982461313092 , -1.038831036157),(2.81948322272412 , -1.03916461252738),(2.819483939873 , -1.03916498744049),(2.81948465132973 , -1.03924903382287),(2.81918790192299 , -1.04167394152271),(2.81885723087258 , -1.04415951515634),(2.81755145707371 , -1.04736273966815),(2.81754976133116 , -1.04736296894416)";
        String[] test2 = test.split(",");
        StringBuilder sb = new StringBuilder("POLYGON J2000 ");
        for(String a : test2){
        	String b = a.replaceAll("\\(", "");
        	String c = b.replaceAll("\\)","");
        	String d = c.trim();
        	Double point = Double.parseDouble(d)*180.0/Math.PI;
        	sb.append(" " + point + " ");
        	
        }
        System.out.println(sb.toString());
        	System.out
        .println("var aladin = A.aladin('#aladin-lite-div', {cooFrame: 'equatorial', survey: 'P/GLIMPSE360', fov: 0.15}); \n"
                + "aladin.gotoRaDec("
                        + cra
                        + ", "
                        + cdec
                        + "); \n");
        
        for(String stcs : stcsPolygon.values()){
        
        System.out
        .println("var overlay = A.graphicOverlay({color: '#ee2345', lineWidth: 3}); \n"
                        + "aladin.addOverlay(overlay); \n"
                        + "var a = aladin.createFootprintsFromSTCS(\""
                        + stcs
                        + "\");\n"
                        + "overlay.addFootprints(a); ");
    	
        }

    }

}
