package ch.heigvd.dil.converter;


import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import picocli.CommandLine.ITypeConverter;
import picocli.CommandLine.TypeConversionException;

/**
 * Gère la conversion d'un chemin de fichier en Path.
 * @author Stéphane Marengo
 */
public class PathDirectoryConverter implements ITypeConverter<Path> {
    @Override
    public Path convert(String str) {
        Path path;
        try {
            path = Path.of(str);
        } catch (InvalidPathException e) {
            throw new TypeConversionException("Invalid path: " + str);
        }

        if (!Files.isDirectory(path)) {
            throw new TypeConversionException("Not a directory: " + str);
        }
        return path;
    }
}
