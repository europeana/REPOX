package pt.utl.ist.util.exceptions;

import java.io.Serializable;

/**
 * Already existent resource.
 * 
 * @author Simon Tzanakis (Simon.Tzanakis@theeuropeanlibrary.org)
 * @since Oct 16, 2014
 */
public class AlreadyExistsException extends Exception implements Serializable {
  private static final long serialVersionUID = 44L;
  private String datasetId = null;

  public AlreadyExistsException() {
    super();
  }

  public AlreadyExistsException(String msg) {
    super(msg);
  }

  public AlreadyExistsException(String msg, String datasetId) {
    super(msg);
    this.datasetId = datasetId;
  }

  public AlreadyExistsException(String msg, Exception e) {
    super(msg, e);
  }
  
  public String getDatasetId() {
    return datasetId;
  }

}
