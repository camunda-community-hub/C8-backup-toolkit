// -----------------------------------------------------------
//
// Dashboard
//
// Manage the dashboard. Root component
//
// -----------------------------------------------------------

import React from 'react';
import {Button, InlineNotification, Select, TextInput} from "carbon-components-react";
import {ArrowRepeat, XCircle} from 'react-bootstrap-icons';

import RestCallService from "../services/RestCallService";



class Dashboard extends React.Component {


  constructor(_props) {
    super();
    this.state = {
      dashboard: {
        details: [],
      },
      display: {
        loading: true
      },


    };
  }

  componentDidMount() {
    this.refreshDashboard();
  }


  render() {
    // console.log("dashboard.render display="+JSON.stringify(this.state.display));
    return (<div className={"container"}>

      <div className="row" style={{width: "100%"}}>
        <div className="col-md-10">
          <h1 className="title">Dashboard</h1>
          <InlineNotification kind="info" hideCloseButton="true" lowContrast="false">
            Give the last backup time and status, if a next backup is scheduled.
          </InlineNotification>
        </div>
      </div>


    </div>)

  }


  refreshDashboard() {
    // let uri = 'blueberry/api/runner/dashboard?period=' + period + "&orderBy=" + orderBy;
    // console.log("DashBoard.refreshDashboard http[" + uri + "]");

    this.setDisplayProperty("loading", true);
    this.setState({status: ""});
    // var restCallService = RestCallService.getInstance();
    // restCallService.getJson(uri, this, this.refreshDashboardCallback);
  }

  refreshDashboardCallback(httpPayload) {
    this.setDisplayProperty("loading", false);
    if (httpPayload.isError()) {
      console.log("Dashboard.refreshDashboardCallback: error " + httpPayload.getError());
      this.setState({status: "Error"});
    } else {
      let firstRunner = httpPayload.getData().details[0];
      console.log("dashboard: RESTCALLBACK first is [" + JSON.stringify(firstRunner.name) + "]");
      this.setState({dashboard: httpPayload.getData()});

    }
  }

  /**
   * Set the display property
   * @param propertyName name of the property
   * @param propertyValue the value
   */
  setDisplayProperty(propertyName, propertyValue) {
    let displayObject = this.state.display;
    displayObject[propertyName] = propertyValue;
    this.setState({display: displayObject});
  }
}

export default Dashboard;
