package org.example.generatecodesum.Service;

import com.example.intelligentengineeringassistant.client.GitHubClient;
import com.example.intelligentengineeringassistant.model.File;
import com.example.intelligentengineeringassistant.model.Project;
import com.example.intelligentengineeringassistant.repository.ProjectRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private GitHubClient gitHubClient;

    @Autowired
    private ObjectMapper objectMapper;

    public Project saveProjectFromGitHub(String owner, String repo) throws Exception {
        String repoInfo = gitHubClient.getRepositoryInfo(owner, repo);
        JsonNode repoJson = objectMapper.readTree(repoInfo);

        Project project = new Project();
        project.setName(repoJson.get("name").asText());
        project.setDescription(repoJson.get("description").asText());
        project.setUrl(repoJson.get("html_url").asText());
        project.setMaintained(true);

        return projectRepository.save(project);
    }

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    public File getRepositoryFileTree(String owner, String repo) {
        try {
            return getRepositoryFileTree(owner, repo, "");
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch repository file tree", e);
        }
    }

    private File getRepositoryFileTree(String owner, String repo, String path) throws Exception {
        List<File> files = gitHubClient.getRepositoryContents(owner, repo, path);

        File root = new File();
        root.setName(path.isEmpty() ? "/" : path);
        root.setPath(path);
        root.setType("dir");
        root.setChildren(new ArrayList<>());

        for (File file : files) {
            if ("dir".equals(file.getType())) {
                // Recursively get the content of the directory
                File childDir = getRepositoryFileTree(owner, repo, file.getPath());
                file.setChildren(childDir.getChildren());
                root.getChildren().add(file);
            } else {
                file.setChildren(null); // No children for files
                root.getChildren().add(file);
            }
        }

        return root;
    }

    public byte[] downloadFile(String owner, String repo, String path, String localPath) throws IOException {
        byte[] fileData = gitHubClient.downloadFile(owner, repo, path);
        try (FileOutputStream fos = new FileOutputStream(localPath)) {
            fos.write(fileData);
        }
        return fileData;
    }

    public byte[] downloadAllFilesAsZip(String owner, String repo) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            File rootFile = getRepositoryFileTree(owner, repo, "");
            addFilesToZip(owner, repo, rootFile, zos, "");
        }
        return baos.toByteArray();
    }

    private void addFilesToZip(String owner, String repo, File file, ZipOutputStream zos, String parentDir) throws IOException {
        if ("dir".equals(file.getType())) {
            for (File child : file.getChildren()) {
                addFilesToZip(owner, repo, child, zos, parentDir + file.getName() + "/");
            }
        } else {
            zos.putNextEntry(new ZipEntry(parentDir + file.getName()));
            byte[] fileData = gitHubClient.downloadFile(owner, repo, file.getPath());
            zos.write(fileData);
            zos.closeEntry();
        }
    }
}
