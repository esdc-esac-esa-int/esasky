#!/bin/bash

echo "build.number=11" > build.number

ant deploy

cp -R war/esaskyweb_V3_0_B11/esaskyweb_V3_0_B11.* /Users/fgiordano/pyesasky/pyesasky/pyesasky/nbextension/static/js/esaskyweb_V3_0/

cp -R war/esaskyweb_V3_0_B11/*.cache.* /Users/fgiordano/pyesasky/pyesasky/pyesasky/nbextension/static/js/esaskyweb_V3_0/

echo "build.number=11" > build.number

