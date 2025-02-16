// -----------------------------------------------------------
//
// Parameters
//
// List of all runners available
//
// -----------------------------------------------------------

import React from 'react';
import {Button, InlineNotification, NumberInput, Select, Tag, TextInput} from "carbon-components-react";
import {ArrowRepeat} from "react-bootstrap-icons";
import RestCallService from "../services/RestCallService";

class Configuration extends React.Component {



  constructor(_props) {
    super();
    this.state = {
      secrets: [],
      display: {loading: false}
    };
  }

  componentDidMount(prevProps) {
    this.refreshConfiguration();
  }

  render() {
    return (
        <div className={"container"}>
          <div className="row" style={{width: "100%"}}>
            <div className="col-md-10">
              <h1 className="title">Configuration</h1>
              <InlineNotification kind="info" hideCloseButton="true" lowContrast="false">
                Current configuration of Blueberry
              </InlineNotification>

            </div>
          </div>


        </div>
    )
  }


  refreshConfiguration() {
    let uri = 'blueberry/api/checkup.';
    console.log("checkup.checkup http[" + uri + "]");

    //this.setDisplayProperty("loading", true);
    //this.setState({status: ""});
    // var restCallService = RestCallService.getInstance();
    // restCallService.getJson(uri, this, this.refreshListBackupCallback);
  }

  refreshListBackupCallback(httpPayload) {
  }

}

export default Configuration;