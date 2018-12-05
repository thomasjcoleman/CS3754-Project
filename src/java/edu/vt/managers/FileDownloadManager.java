package edu.vt.managers;

import edu.vt.EntityBeans.UserFile;
import edu.vt.controllers.UserFileController;
import javax.inject.Inject;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Serializable;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.ServletContext;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

@Named(value = "fileDownloadManager")
@RequestScoped

public class FileDownloadManager implements Serializable {
    /*
    ===============================
    Instance Variables (Properties)
    ===============================
     */

    @Inject
    private UserFileController userFileController;

    /*
    StreamedContent and DefaultStreamedContent classes are imported from
    org.primefaces.model.StreamedContent above.
     */
    private StreamedContent file;

    /*
    ==================
    Constructor Method
    ==================
     */
    public FileDownloadManager() {
    }

    /*
    =============
    Getter Method
    =============
     */
    public StreamedContent getFile() throws FileNotFoundException {

        UserFile fileToDownload = userFileController.getSelected();

        String nameOfFileToDownload = fileToDownload.getFilename();
        String absolutePathOfFileToDownload = fileToDownload.getFilePath();
        String contentMimeType = FacesContext.getCurrentInstance().getExternalContext().getMimeType(absolutePathOfFileToDownload);

        InputStream stream = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().
                getContext()).getResourceAsStream(absolutePathOfFileToDownload);

        // Obtain the filename without the prefix "userId_" inserted to associate the file to a user
        String downloadedFileName = nameOfFileToDownload.substring(nameOfFileToDownload.indexOf("_") + 1);

        // FileInputStream must be used here since our files are stored in a directory external to our application
        file = new DefaultStreamedContent(new FileInputStream(absolutePathOfFileToDownload), contentMimeType, downloadedFileName);

        return file;
    }

}