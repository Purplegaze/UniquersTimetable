package usecase.deletesection;

/**
 * Output Boundary for Delete Course use case.
 */
public interface DeleteSectionOutputBoundary {
    /**
     * Present successful deletion result.
     */
    void presentSuccess(DeleteSectionOutputData outputData);
    
    /**
     * Present error message.
     */
    void presentError(String errorMessage);
}
