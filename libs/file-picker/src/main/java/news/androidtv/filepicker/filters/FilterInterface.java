package news.androidtv.filepicker.filters;

import news.androidtv.filepicker.model.AbstractFile;

/**
 * Created by Nick on 9/21/2016.
 */
public interface FilterInterface {
    /**
     * Checks if the provided {@link AbstractFile} matches the filter's parameters
     * @param file The file to check
     * @return true if the filter matches. Otherwise the file will not be shown.
     */
    boolean matches(AbstractFile file);

    /**
     * Returns a short description of what this filter is trying to find, to be displayed to the
     * user.
     * @return A short, three to four word description.
     */
    String getDescription();
}
