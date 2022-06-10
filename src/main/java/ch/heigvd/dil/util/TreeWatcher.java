package ch.heigvd.dil.util;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardWatchEventKinds.*;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;

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
    private CompletableFuture<Void> future;
    private boolean isRunning;

    /**
     * Interface pour les callbacks.
     */
    public interface ChangeListener {
        /**
         * Callback appelée lorsque des modifications sont observées.
         * @param modified les fichiers modifiés
         * @param added les fichiers ajoutés
         * @param deleted les fichiers supprimés
         */
        void onChange(Path[] modified, Path[] added, Path[] deleted);
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
     * @throws IOException si une erreur I/O survient
     */
    public void start() throws IOException {
        if (isRunning) return;

        watcher = FileSystems.getDefault().newWatchService();
        keys = new HashMap<>();

        try {
            registerRoot(root);
        } catch (IOException e) {
            throw new IOException("Cannot register directory: " + e.getMessage(), e);
        }

        // Suppression des événements déjà en attente.
        keys.forEach((key, path) -> key.pollEvents());

        future = CompletableFuture.runAsync(this::run);
        isRunning = true;
    }

    /**
     * Arrête le watcher.
     * @throws IOException si une erreur I/O survient
     */
    public void stop() throws IOException {
        if (!isRunning) return;

        future.cancel(true);
        try {
            future.join();
        } catch (CancellationException ignored) {

        } finally {
            watcher.close();
        }
        isRunning = false;
    }

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

            List<Path> added = new LinkedList<>();
            List<Path> modified = new LinkedList<>();
            List<Path> removed = new LinkedList<>();
            processEvents(key, dir, added, modified, removed);

            boolean valid = key.reset();
            if (!valid) {
                keys.remove(key);
                if (keys.isEmpty()) {
                    break;
                }
            }

            if (!added.isEmpty() || !modified.isEmpty() || !removed.isEmpty()) {
                listener.onChange(
                        added.toArray(Path[]::new), modified.toArray(Path[]::new), removed.toArray(Path[]::new));
            }
        }
    }

    /**
     * Traite les événements de la clé donnée.
     *
     * @param key      la clé à traiter
     * @param dir      le dossier associé à la clé
     * @param added la liste des fichiers ajoutés
     * @param modified la liste des fichiers modifiés
     * @param deleted la liste des fichiers supprimés
     */
    private void processEvents(WatchKey key, Path dir, List<Path> added, List<Path> modified, List<Path> deleted) {
        var events = key.pollEvents();
        var it = events.iterator();

        for (int i = 0; it.hasNext(); i++) {
            WatchEvent<?> event = it.next();
            WatchEvent.Kind<?> kind = event.kind();

            if (kind == OVERFLOW) {
                continue;
            }

            WatchEvent<Path> ev = (WatchEvent<Path>) event;
            Path path = dir.resolve(ev.context());

            if (kind == ENTRY_CREATE) {
                added.add(path);

                if (Files.isDirectory(path, NOFOLLOW_LINKS)) {
                    try {
                        registerRoot(path);
                    } catch (IOException x) {
                        System.err.println("Failed to register directory " + path + ": " + x);
                    }
                }
            } else if (kind == ENTRY_MODIFY) {
                modified.add(path);
            } else if (kind == ENTRY_DELETE) {
                deleted.add(path);
            }
        }
    }
}
