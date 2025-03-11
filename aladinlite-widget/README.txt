        Application Description
      ~~~~~~~~~~~~~~~~~~~~~~~~~~~

Project structure template for a GWT widget library


        Directory Structure
      ~~~~~~~~~~~~~~~~~~~~~~~

build/
        Contains the compiled 'classes' and the generated 'javadoc' files.
        This directory should not be included in version control (Subversion).

dist/
        Contains the distributable GWT widget library JAR file.
        This directory should not be included in version control (Subversion).

docs/
        General documentation for the GWT widget library (description, instructions, ...)

final/
        Contains the final distributable GWT widget library JAR file ant the external 
        libraries it depends on. 
        This directory should not be included in version control (Subversion).

lib/
        External libraries specifically required by this GWT widget library.
        The filename should include the version as such: libname-vX.x.jar
	      This directory should not be included in version control (Subversion).

setup/
        GWT widget library setup files include server configuration, database creation, ...

src/conf/
        The config files include the manifest file and other dynamic configurations.

src/java/
        The Java sources in packages: esac.archive.eacs.template.gwt[.whatever]

test/
        GWT widget library test files include the JUnit test sources in parallel packages.

war/
        This directory is for testing the GWT component only. It contains static 
        resources that can be served publicly, such as image files, stylesheets and
        HTML Host Pages. It also contains the compiled 'classes' required to run the 
        GWT Development Mode server and output files from the GWT compiler to deploy
        the resulting files (HTML, stylesheets, javascript and images) to a public 
        web server if needed.

build.properties
        User specific build property file. Supersedes the defaults property file.
        This file should not be included in version control (Subversion).

build.properties.defaults
        Defaults build property file. Defines the properties for a standard build.

build.xml
        ANT build script for the GWT widget library. Run 'ant -projecthelp' for more info.
	
HOWTO.txt
        This file describes how to import the GWT widget library into Eclipse to make
        use of features the GWT Plugin for Eclipse provides (debugging, compilation,...).

ivy-tasks.xml
        ANT build script for the GWT widget library. Contains ant tasks to allow the use 
        of Ivy.
	
ivy.xml
        Ivy dependency file. Describes project details and library dependencies. 

README.txt
        This file describes the GWT widget library and its directory structure.
