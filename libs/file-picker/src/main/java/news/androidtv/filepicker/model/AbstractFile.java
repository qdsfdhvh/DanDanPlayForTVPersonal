package news.androidtv.filepicker.model;

import android.net.Uri;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>An abstract representation of a file with a few simple properties. This class cannot be
 * created on its own. It is meant to be subclassed and implemented with a variety of different
 * file-based implementations, locally and through the cloud.</p>
 *
 * <p>There are some static methods, such as {@link #fromFile(File)} which can create an
 * AbstractFilefrom a local file, although this class was designed to work with other types of file
 * structures where a {@link File} may not exist or be accessible.</p>
 */
public class AbstractFile {
    protected boolean mIsDirectory;
    protected String mTitle;
    protected Uri mUri;
    protected AbstractFile parent;
    protected AbstractFile[] mDirectoryFiles;

    protected AbstractFile() {
    }

    /**
     * Returns whether this is a directory, folder, or collection of files.
     *
     * @return true if this is not a file, but a directory of files
     */
    public boolean isDirectory() {
        return mIsDirectory;
    }

    /**
     * Returns the display name for this file. This may be the folder or file name without the
     * entire file path.
     *
     * @return The displayed name for this file.
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * Returns the Uri for this file. This may be the absolute file path name for this file, or
     * some other type of pointer depending on the implementation.
     *
     * @return The Uri for the file.
     */
    public Uri getUri() {
        return mUri;
    }

    /**
     * For a directory, returns a list of AbstractFiles.
     *
     * @return The list of AbstractFiles found inside a directory. If this is not a directory, null
     * will be returned.
     */
    public AbstractFile[] getFiles() {
        if (mIsDirectory) {
            return mDirectoryFiles;
        }
        return null;
    }

    /**
     * Returns the parent directory for this file if possible.
     *
     * @return An AbstractFile which represents the directory this file is in.
     */
    public AbstractFile getParent() {
        return parent;
    }

    /**
     * Returns the type of file this is. This can be overriden for a specific purpose. Its default
     * implementation will check the last section of the file's title and use that.
     *
     * @return The file extension for the file.
     */
    public String getFileExtension() {
        String[] uriArray = mTitle.split("[.]");
        return uriArray[uriArray.length-1];
    }

    /**
     * Passing a local folder from the device will allow a list of files to be instantiated with
     * default parameters.
     *
     * @param filePath The local file path to convert
     * @return A list of AbstractFiles representing the contents of this folder.
     */
    public static AbstractFile fromLocalPath(File filePath) {
        List<AbstractFile> abstractFileList = new ArrayList<>();
        AbstractFile abstractFile = fromFile(filePath);
        if (filePath.listFiles() != null) {
            for (File file : filePath.listFiles()) {
                AbstractFile childFile = fromFile(file);
                childFile.parent = abstractFile;
                abstractFileList.add(childFile);
            }
        }
        abstractFile.mDirectoryFiles =
                abstractFileList.toArray(new AbstractFile[abstractFileList.size()]);
        return abstractFile;
    }

    /**
     * Creates a new AbstractFile based on the attributes of the provided File object.
     *
     * @param file The file to be converted.
     * @return An AbstractFile representing the file.
     */
    public static AbstractFile fromFile(File file) {
        AbstractFile abstractFile = new AbstractFile();
        abstractFile.mIsDirectory = file.isDirectory();
        abstractFile.mTitle = file.getName();
        abstractFile.mUri = Uri.fromFile(file);
        return abstractFile;
    }

    /**
     * When a directory is provided, this will open that directory based on {@link #getUri()} and
     * return a list of AbstractFiles from that directory.
     *
     * @param directory The directory to explore, as an AbstractFile.
     * @return A list of AbstractFiles representing the contents of this folder.
     */
    public static AbstractFile fromLocalAbstractFile(AbstractFile directory) {
//        File directoryFile = new File(String.valueOf(directory.getUri()));
        File directoryFile = new File(directory.getUri().getPath());

        AbstractFile abstractDirectory = fromLocalPath(directoryFile);
        if (directoryFile.getParentFile() != null) {
            abstractDirectory.parent = fromFile(directoryFile.getParentFile());
        }
        return abstractDirectory;
    }
}
