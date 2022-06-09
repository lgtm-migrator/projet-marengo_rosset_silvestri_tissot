package ch.heigvd.dil.util;


import java.io.File;
import java.io.IOException;
import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

public class RepositoryHandler {
    private Git git;
    private final String temporaryPath;

    public RepositoryHandler(String path) {
        temporaryPath = path;
    }

    public void createRepository() throws IOException {
        // Creer un nouveau depot
        /*Repository tmp = FileRepositoryBuilder.create(
                new File(temporaryPath));
        tmp.create();*/

        // Ouvrir un depot existant
        Repository destinationRepo =
                new FileRepositoryBuilder().setGitDir(new File(temporaryPath)).build();
        git = new Git(destinationRepo);
    }

    public void transfertFile() throws GitAPIException {
        /*PullCommand pullCmd = git.pull();
        pullCmd.setCredentialsProvider(new UsernamePasswordCredentialsProvider("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", ""));
        pullCmd.setRemote(" origin/main").call();*/

        AddCommand add = git.add();
        add.addFilepattern(".").call();

        CommitCommand commit = git.commit();
        commit.setMessage("Ajout").call();

        // push to remote:
        PushCommand pushCommand = git.push();
        pushCommand.setCredentialsProvider(
                new UsernamePasswordCredentialsProvider("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", ""));
        // you can add more settings here if needed
        pushCommand
                .setRemote("https://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@github.com/loicrheig/test-repo-dil")
                .call();

        // Il faut désactiver gpg : git config --global commit.gpgsign false !!!
    }
}
