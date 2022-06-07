package ch.heigvd.dil.util;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.Map;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardWatchEventKinds.*;

/**
 * Classe utilitaire permettant de surveiller une arborescence de fichiers de façon asynchrone.
 * <p>
 * Remarque : Adapté de
 * <a href="https://docs.oracle.com/javase/tutorial/essential/io/examples/WatchDir.java">la documentation oracle</a>
 * @author Marengo Stéphane
 */
public class TreeWatcher {
    private final Path root;
    private final Path[] ignoredPaths;
    private final ChangeListener listener;
    private WatchService watcher;
    private Map<WatchKey, Path> keys;
    private boolean isRunning;

    /**
     * Interface pour les callbacks.
     */
    public interface ChangeListener {
        /**
         * Callback appelée lorsque des modifications sont observées.
         * @param paths les chemins des fichiers modifiés.
         */
        void onChange(Path[] paths);
    }

    /**
     * Créé un watcher récursif sur le dossier spécifié.
     * @param root le dossier racine
     * @param callback le callback à appeler lorsqu'un changement est détecté
     * @param ignoredPaths les chemins à ignorer
     */
    public TreeWatcher(Path root, ChangeListener callback, Path... ignoredPaths) {
        this.root = root;
        listener = callback;
        isRunning = false;
        this.ignoredPaths = Arrays.copyOf(ignoredPaths, ignoredPaths.length);
    }

    /**
     * Démarre le watcher.
     */
    public void start() {}

    /**
     * Arrête le watcher.
     */
    public void stop() {}

    /**
     * Enregistre l'arborescence complète du dossier passé en paramètre dans le watcher.
     *
     * @param root         le dossier racine
     * @throws IOException si une erreur I/O survient
     */
    private void registerRoot(Path root) throws IOException {
        Files.walkFileTree(root, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                if (isIgnored(dir)) {
                    return FileVisitResult.SKIP_SUBTREE;
                }
                register(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    /**
     * Détermine si un chemin fait partie des chemins ignorés.
     * @param dir le chemin à tester
     * @return vrai si le chemin doit être ignoré, faux sinon
     */
    private boolean isIgnored(Path dir) {
        for (Path ignoredPath : ignoredPaths) {
            if (ignoredPath.equals(dir)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Enregistre le dossier passé en paramètre dans le watcher.
     * @param dir le dossier à enregistrer
     * @throws IOException si une erreur I/O survient
     */
    private void register(Path dir) throws IOException {
        WatchKey key = dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
        keys.put(key, dir);
    }

    /**
     * Attend les événements et les traite.
     */
    private void run() {
        while (true) {
            WatchKey key;
            try {
                key = watcher.take();
            } catch (InterruptedException x) {
                return;
            }

            Path dir = keys.get(key);
            if (dir == null) {
                continue;
            }

            Path[] children = processEvents(key, dir);

            boolean valid = key.reset();
            if (!valid) {
                keys.remove(key);
                if (keys.isEmpty()) {
                    break;
                }
            }

            if (children.length > 0) listener.onChange(children);
        }
    }

    /**
     * Traite les événements de la clé donnée.
     * @param key la clé à traiter
     * @param dir le dossier associé à la clé
     * @return les chemins des fichiers modifiés
     */
    private Path[] processEvents(WatchKey key, Path dir) {
        var events = key.pollEvents();
        Path[] children = new Path[events.size()];
        var it = events.iterator();

        for (int i = 0; it.hasNext(); i++) {
            WatchEvent<?> event = it.next();
            WatchEvent.Kind<?> kind = event.kind();

            if (kind == OVERFLOW) {
                continue;
            }

            WatchEvent<Path> ev = (WatchEvent<Path>) event;
            Path child = dir.resolve(ev.context());
            children[i] = child;

            if (kind == ENTRY_CREATE && Files.isDirectory(child, NOFOLLOW_LINKS)) {
                try {
                    registerRoot(child);
                } catch (IOException x) {
                    System.err.println("Failed to register directory " + child + ": " + x);
                }
            }
        }
        return children;
    }
}
