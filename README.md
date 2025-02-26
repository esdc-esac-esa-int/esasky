# ESASky

This is the ESASky frontend. This is the code that is running at [the ESASky website](https://sky.esa.int/esasky/).

## Develop

### Install

In a terminal, run this command:

```bash
mvn clean install -f pom.xml -Plocal
```

The last argument, `local` in the example above, references the configuration file that will be used when building the project. This requires there to be a corresponding configuration file here: `esasky-cl/src/main/filters/local.properties`.

### Configure

You can configure your ESASky instance by editing the property file found at `esasky-cl/src/main/filters/local.properties`.

### Run locally

ESASky is typically deployed with Tomcat. Start Tomcat and add the generated war file (found at `esasky-cl/target/esasky-cl-<version>.war`). You should now have a running ESASky instance at port 8080: http://localhost:8080/esasky/.

## License

This work is released under the GNU AGPL license (see the separate license file for details).
