<%@page import="java.io.BufferedReader"%>
<%@page import="java.io.FileReader"%>
<%@page import="java.io.File"%>
<%@page import="java.net.URLEncoder"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<% 
	FileReader fr = new FileReader(application.getRealPath("/") + "index.html");
	BufferedReader br = new BufferedReader(fr);
	String line = null;
	while ((line = br.readLine()) != null) {
		 if (line.startsWith("<!--#METATAGS#")) {
		    
			    try {
				
					/* 
					   Following line is parsed from jsp servlet to set the OG Meta tags,
					   parameters are separated by #, in this order: 
					   PageTitle, defaultImageURL, skyImageServiceBaseUrl, imgWidth, imgHeight
					   The line must start with # and end with # 
				    */
				   
					final String[] params = line.split("#");
					final String title = (params.length > 2) ? params[2] : "ESASky";
					final String esaSkyUrl = "//sky.esa.int/" + ((request.getQueryString() != null) ? "?"+ request.getQueryString() : "");
					String skyImageUrl = (params.length > 3) ? params[3] : "//sky.esa.int/images/fbESASky.png";
					
					final int imgWidth = (params.length > 5) ? Integer.parseInt(params[5]) : 1200;
					final int imgHeight = (params.length > 6) ? Integer.parseInt(params[6]) : 630;
					
					final String targetParam = request.getParameter("target");
					final String fovParam = request.getParameter("fov");
					final String hipsParam = request.getParameter("hips");
					
					if ((targetParam != null)
				        && (targetParam != null)
				        && (targetParam != null)) {
		
					    final String skyImageBaseUrl = (params.length > 4) ? params[4] : "//sky.esa.int/esasky-tap/skyimage";
						final double aspectRatio = (double)imgWidth/(double)imgHeight;
			        
			        		skyImageUrl = skyImageBaseUrl 
			                            + "?target=" + URLEncoder.encode(targetParam, "UTF-8")
			                            + "&hips=" + URLEncoder.encode(hipsParam, "UTF-8") 
			                            + "&fov=" + URLEncoder.encode(fovParam, "UTF-8")
			                            + "&size=" + Math.max(imgWidth, imgHeight) 
			                            + "&aspectratio=" + aspectRatio;
					}
				%>
				
				<meta property="fb:app_id" content="2064431993832471" />
		        <meta property="og:url" content="<%= esaSkyUrl %>" />
				<meta property="og:type" content="website" />
				<meta property="og:locale" content="en_US" />
				<meta property="og:title" content="<%= title %>" />
				<meta property="og:description" content="ESASky is an application that allows you to visualise and download public astronomical data from space-based missions." />
				<meta property="og:image" content="<%= skyImageUrl %>" />
				<meta property="og:image:type" content="image/png" />
				<meta property="og:image:width" content="<%= imgWidth %>" />
				<meta property="og:image:height" content="<%= imgHeight %>" />
			<%
		
		    } catch (Exception ex){
		        //DO NOTHING
		    }
				
		} else {%>
			<%= line %>
		<%}
	}
	
	br.close();
%>
