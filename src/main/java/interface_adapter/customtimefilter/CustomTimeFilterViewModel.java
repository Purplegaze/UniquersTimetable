package interface_adapter.customtimefilter;

import interface_adapter.viewmodel.SearchResultViewModel;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CustomTimeFilterViewModel {

    public static final String PROPERTY_RESULTS = "results";
    public static final String PROPERTY_NO_RESULTS = "noResults";
    public static final String PROPERTY_ERROR = "errorMessage";

    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    private List<SearchResultViewModel> results = new ArrayList<>();
    private boolean noResults;
    private String errorMessage;

    public List<SearchResultViewModel> getResults() {
        return results;
    }

    public void setResults(List<SearchResultViewModel> results) {
        List<SearchResultViewModel> old = this.results;
        this.results = results != null ? results : Collections.emptyList();
        support.firePropertyChange(PROPERTY_RESULTS, old, this.results);
    }

    public boolean isNoResults() {
        return noResults;
    }

    public void setNoResults(boolean noResults) {
        boolean old = this.noResults;
        this.noResults = noResults;
        support.firePropertyChange(PROPERTY_NO_RESULTS, old, noResults);
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        String old = this.errorMessage;
        this.errorMessage = errorMessage;
        support.firePropertyChange(PROPERTY_ERROR, old, errorMessage);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }
}