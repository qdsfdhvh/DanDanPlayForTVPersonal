package news.androidtv.filepicker.filters;

import news.androidtv.filepicker.model.AbstractFile;

/**
 * A generic implementation of a {@link FilterInterface} that returns true in all instances.
 */
public class EmptyFilter implements FilterInterface {
    @Override
    public boolean matches(AbstractFile file) {
        return true;
    }

    @Override
    public String getDescription() {
        return "Select a file";
    }
}
