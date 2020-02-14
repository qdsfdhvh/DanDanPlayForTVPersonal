package news.androidtv.filepicker.filters;

import news.androidtv.filepicker.model.AbstractFile;

/**
 * A type of {@link FilterInterface} that only matches with directories.
 */
public class DirectoryFilter implements FilterInterface {
    @Override
    public boolean matches(AbstractFile file) {
        return file.isDirectory();
    }

    @Override
    public String getDescription() {
        return "Select a directory";
    }
}
