java -Xmx800m  -Djava.ext.dirs=ext-lib:ext-lib/dom4j:ext-lib/derby:ext-lib/xerces -cp .:dist/Sanchay.jar:build/classes -Djava.security.policy=policy sanchay.sim.surface.ExtractBySurfaceSimilarity $@
