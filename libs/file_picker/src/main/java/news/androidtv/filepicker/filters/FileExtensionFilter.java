package news.androidtv.filepicker.filters;

import news.androidtv.filepicker.model.AbstractFile;

/**
 * This filter makes it easy to match with files that have a given file extension.
 */
public class FileExtensionFilter implements FilterInterface {
    private String mFileExtension;

    /**
     * Creates a new instance of a FileExtensionFilter
     *
     * @param fileExtension The file extension to be selected, without a period.
     */
    public FileExtensionFilter(String fileExtension) {
        mFileExtension = fileExtension;
    }

    @Override
    public boolean matches(AbstractFile file) {
        return file.getFileExtension().equals(mFileExtension) || file.isDirectory();
    }

    @Override
    public String getDescription() {
        return "Select a " + mFileExtension + " file";
    }
}
