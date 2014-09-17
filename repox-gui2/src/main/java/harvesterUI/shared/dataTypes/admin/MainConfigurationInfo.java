package harvesterUI.shared.dataTypes.admin;

import harvesterUI.shared.ProjectType;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Created to Project REPOX
 * User: Edmundo
 * Date: 18-07-2012
 * Time: 16:53
 */
public class MainConfigurationInfo implements IsSerializable {

    private ProjectType projectType;
    private String defaultExportFolder;
    private String repositoryFolderPath;

    public MainConfigurationInfo() {}

    public MainConfigurationInfo(ProjectType projectType, String repositoryFolderPath) {
        this.projectType = projectType;
        this.repositoryFolderPath = repositoryFolderPath;
    }

    public ProjectType getProjectType() {
        return projectType;
    }

    public void setDefaultExportFolder(String defaultExportFolder) {
        this.defaultExportFolder = defaultExportFolder;
    }

    public String getDefaultExportFolder() {
        if(defaultExportFolder == null)
            return "";
        return defaultExportFolder;
    }

    public String getRepositoryFolderPath() {
        return repositoryFolderPath;
    }
}
