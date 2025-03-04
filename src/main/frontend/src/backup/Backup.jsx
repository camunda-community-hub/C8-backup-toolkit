// -----------------------------------------------------------
//
// Parameters
//
// List of all runners available
//
// -----------------------------------------------------------

import React from 'react';
import RestCallService from "../services/RestCallService";
import ControllerPage from "../component/ControllerPage";

import {Button, InlineNotification} from "carbon-components-react";
import {ArrowRepeat} from "react-bootstrap-icons";


class Backup extends React.Component {


    constructor(_props) {
        super();

        this.state = {
            display: {loading: false},
            backup: {status: ""},
            error: null
        };
    }

    componentDidMount() {
        this.monitorBackup();
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

                <div className="row" style={{width: "100%"}}>
                    <div className="col-md-12">
                        <ControllerPage errorMessage={this.state.status} loading={this.state.display.loading}/>
                    </div>
                </div>

                {this.state.backup.status !== '' ? (
                    <div className="alert alert-info" style={{margin: "10px 10px 10px 10px"}}>
                        {this.state.backup.status} {this.state.backup.backupId}
                    </div>
                ) : <div/>
                }


                <div className="row" style={{marginTop: "10px"}}>
                    <div className="col-md-2">
                        <Button className="btn btn-warning btn-sm"
                                onClick={() => {
                                    this.startBackup()
                                }}
                                disabled={this.state.display.loading}>
                            Start a backup
                        </Button>
                    </div>
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
                        <th>ID</th>
                        <th>Name</th>
                        <th>Date</th>
                        <th>Status</th>
                    </tr>
                    </thead>
                    <tbody>
                    {this.state.listBackup ? this.state.listBackup.map((content, _index) =>
                        <tr>
                            <td>{content.backupId}</td>
                            <td>{content.backupName}</td>
                            <td>{content.backupTime}</td>
                            <td>{content.backupStatus}</td>
                        </tr>
                    ) : <div/>
                    }
                    </tbody>
                </table>
            </div>
        )
    }

    monitorBackup() {
        let uri = '/blueberry/api/backup/monitor?';
        console.log("backup.refresh http[" + uri + "]");

        var restCallService = RestCallService.getInstance();
        restCallService.getJson(uri, this, this.monitorBackupCallback);
    }

    monitorBackupCallback(httpPayload) {
        if (httpPayload.isError()) {
            console.log("Backup.monitorBackupCallback: error " + httpPayload.getError());
            this.setState({status: "Error"});
        } else {
            this.setState({backup: httpPayload.getData()})
        }
    }


    refreshListBackup() {
        let uri = '/blueberry/api/backup/list?';
        console.log("backup.refresh http[" + uri + "]");

        this.setDisplayProperty("loading", true);
        this.setState({status: ""});
        var restCallService = RestCallService.getInstance();
        restCallService.getJson(uri, this, this.refreshListBackupCallback);
    }

    refreshListBackupCallback(httpPayload) {
        this.setDisplayProperty("loading", false);
        if (httpPayload.isError()) {
            console.log("Backup.monitorBackupCallback: error " + httpPayload.getError());
            this.setState({status: "Error"});
        } else {
            this.setState({listBackup: httpPayload.getData()})
        }
    }



    startBackup() {
        let uri = '/blueberry/api/backup/start?';
        console.log("backup.startBackup http[" + uri + "]");
        this.setDisplayProperty("loading", true);
        this.setState({status: ""});
        var restCallService = RestCallService.getInstance();
        restCallService.postJson(uri, {}, this, this.startBackupCallback);
    }

    startBackupCallback(httpPayload) {
        debugger;
        this.setDisplayProperty("loading", false);
        if (httpPayload.isError()) {
            console.log("Backup.startBackupCallback: error " + httpPayload.getError());
            this.setState({status: "Error"});
        } else {
            this.setState({backup: httpPayload.getData()})
        }
    }

    /* Set the display property
  * @param propertyName name of the property
  * @param propertyValue the value
  */
    setDisplayProperty(propertyName, propertyValue) {
        let displayObject = this.state.display;
        displayObject[propertyName] = propertyValue;
        this.setState({display: displayObject});
    }

}

export default Backup;