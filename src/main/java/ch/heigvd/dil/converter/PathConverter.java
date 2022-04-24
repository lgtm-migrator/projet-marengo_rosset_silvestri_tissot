package ch.heigvd.dil.converter;


import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import picocli.CommandLine.ITypeConverter;
import picocli.CommandLine.TypeConversionException;

/**
 * Gère la conversion d'un chemin de fichier en Path.
 * @author Loïc Rosset
 */
public class PathConverter implements ITypeConverter<Path> {
    @Override
    public Path convert(String str) {
        Path path;
        try {
            path = Path.of(str);
        } catch (InvalidPathException e) {
            throw new TypeConversionException("Invalid path: " + str);
        }
        return path;
    }
}
