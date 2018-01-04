package com.github.sh0nk.solr.sudachi;

import org.apache.lucene.analysis.util.ResourceLoader;
import org.apache.solr.core.SolrResourceLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ResourceLoaderHelper {

    private final SolrResourceLoader resourceLoader;

    public ResourceLoaderHelper(ResourceLoader resourceLoader) {
        this.resourceLoader = (SolrResourceLoader) resourceLoader;
    }

    // TODO: Need to promise this contract?
    private Path checkPathIsSafe(Path pathToCheck) throws IOException {
        if (Boolean.getBoolean("solr.allow.unsafe.resourceloading"))
            return pathToCheck;
        pathToCheck = pathToCheck.normalize();
        if (pathToCheck.startsWith(resourceLoader.getInstancePath()))
            return pathToCheck;
        throw new IOException("File " + pathToCheck + " is outside resource loader dir " + resourceLoader.getInstancePath() +
                "; set -Dsolr.allow.unsafe.resourceloading=true to allow unsafe loading");
    }

    public Path getResourcePath(String resource) throws IOException {
        Path inConfigDir = resourceLoader.getInstancePath().resolve("conf").resolve(resource);
        if (Files.exists(inConfigDir) && Files.isReadable(inConfigDir)) {
            // If the resource is absolute path, then it is returned as it is
            return inConfigDir;
        }

        Path inInstanceDir = resourceLoader.getInstancePath().resolve(resource);
        if (Files.exists(inInstanceDir) && Files.isReadable(inInstanceDir)) {
            return inInstanceDir;
        }

        throw new IOException("Resource file " + resource + " does not exist either on " + inConfigDir
                + " or on " + inInstanceDir);
    }

}
