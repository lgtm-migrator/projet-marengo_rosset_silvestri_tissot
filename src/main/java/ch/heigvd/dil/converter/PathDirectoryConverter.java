package ch.heigvd.dil.converter;


import java.nio.file.Files;
import java.nio.file.Path;
import picocli.CommandLine.TypeConversionException;

/**
 * Gère la conversion d'un chemin de fichier en Path pour les dossiers.
 * @author Stéphane Marengo
 */
public class PathDirectoryConverter extends PathConverter {
    @Override
    public Path convert(String str) {
        Path path = super.convert(str);

        if (!Files.isDirectory(path)) {
            throw new TypeConversionException("Not a directory: " + str);
        }
        return path;
    }
}
