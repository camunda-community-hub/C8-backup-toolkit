// -----------------------------------------------------------
//
// Content
//
// List of all jar file uploaded
//
// -----------------------------------------------------------

import React, {createRef} from 'react';
import {Button,  InlineNotification} from "carbon-components-react";
import {ArrowRepeat} from "react-bootstrap-icons";
import RestCallService from "../services/RestCallService";

class Checkup extends React.Component {


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
            <h1 className="title">Checkup</h1>
            <InlineNotification kind="info" hideCloseButton="true" lowContrast="false">
              Verify the configuration, to detect any mis configuration.
              <li> Does each component define correctly backup information</li>
              <li> Does the configuration exist, i.e. Elasticsearch component are correctly defined?</li>
              </InlineNotification>

          </div>
        </div>
        <div className="row" style={{marginTop: "10px"}}>
          <div className="col-md-2">
            <Button className="btn btn-success btn-sm"
                    onClick={() => {
                      this.checkup()
                    }}
                    disabled={this.state.display.loading}>
              <ArrowRepeat/> Checkup
            </Button>
          </div>
        </div>
      </div>

    )
  }


  checkup() {
    let uri = 'blueberry/api/checkup.';
    console.log("checkup.checkup http[" + uri + "]");

    this.setDisplayProperty("loading", true);
    this.setState({status: ""});
    var restCallService = RestCallService.getInstance();
    restCallService.getJson(uri, this, this.refreshCheckupCallback);
  }

  refreshCheckupCallback(httpPayload) {
  }



}

export default Checkup;