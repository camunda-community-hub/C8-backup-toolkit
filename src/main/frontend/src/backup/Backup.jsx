// -----------------------------------------------------------
//
// Parameters
//
// List of all runners available
//
// -----------------------------------------------------------

import React from 'react';
import RestCallService from "../services/RestCallService";
import {Button, InlineNotification, TextInput, Toggle} from "carbon-components-react";
import {ArrowRepeat, ExclamationTriangle} from "react-bootstrap-icons";



class Backup extends React.Component {


  constructor(_props) {
    super();

    this.state = {
      display: {loading:false}
    };
  }

  componentDidMount() {
  }

  /*           {JSON.stringify(this.state.runners, null, 2) } */
  render() {
    return (
        <div className={"container"}>
          <div className="row" style={{width: "100%"}}>
            <div className="col-md-10">
              <h1 className="title">Backup</h1>
              <InlineNotification kind="info" hideCloseButton="true" lowContrast="false">
                Last backup are listed.
              </InlineNotification>

            </div>
          </div>

          <div className="row" style={{marginTop: "10px"}}>
            <div className="col-md-2">
              <Button className="btn btn-success btn-sm"
                      onClick={() => {
                        this.refreshListBackup()
                      }}
                      disabled={this.state.display.loading}>
                <ArrowRepeat/> Refresh
              </Button>
            </div>
          </div>

          <table id="runnersTable" className="table is-hoverable is-fullwidth">
            <thead>
            <tr>
              <th>Backup ID</th>
              <th>Date</th>
              <th>Status</th>
            </tr>
            </thead>
            <tbody>
            </tbody>
          </table>
        </div>


  )
  }


  refreshListBackup() {
    let uri = 'blueberry/api/checkup.';
    console.log("checkup.checkup http[" + uri + "]");

    this.setDisplayProperty("loading", true);
    this.setState({status: ""});
    var restCallService = RestCallService.getInstance();
    restCallService.getJson(uri, this, this.refreshListBackupCallback);
  }

  refreshListBackupCallback(httpPayload) {
  }


}

export default Backup;