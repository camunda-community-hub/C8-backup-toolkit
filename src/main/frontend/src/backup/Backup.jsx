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

import {Button, Checkbox, InlineNotification, TextInput} from "carbon-components-react";
import {ArrowRepeat} from "react-bootstrap-icons";


class Backup extends React.Component {


    constructor(_props) {
        super();

        this.state = {
            display: {loading: false},
            parameter: {explicit: false, backupId: ''},
            resultCall:{ status:200, error:"", message:"", component:""},
            listBackup: []

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
                        <ControllerPage
                            error={`${this.state.resultCall.component} - ${this.state.resultCall.error}`}
                            errorMessage={this.state.resultCall.message}
                            loading={this.state.display.loading}/>
                    </div>
                </div>



                <div className="row" style={{marginTop: "10px"}}>
                    <div className="col-md-2  d-flex align-items-end">
                        <div>
                            <Checkbox
                                id="chooseBackup"
                                labelText="Give explicit backupId"
                                checked={this.state.parameter.explicit}
                                onChange={(event) => {
                                    var parameter = this.state.parameter;
                                    console.log("Parameters=[" + parameter + "] checked=" + event.target.checked);
                                    parameter.explicit = event.target.checked;
                                    this.setState({"parameter": parameter})
                                }}
                            />

                            <TextInput
                                className="m-3"
                                labelText="Explicit ID (number only)"
                                disabled={!this.state.parameter.explicit}
                                value={this.state.parameter.backupId}
                                type="number"
                                onChange={(event) => {
                                    var parameter = this.state.parameter;
                                    parameter.backupId = event.target.value;
                                    this.setState({"parameter": parameter})
                                }}
                                placeholder="Enter some text"
                            />

                            <Button className="btn btn-warning btn-sm"
                                    onClick={() => {
                                        this.startBackup()
                                    }}
                                    disabled={this.state.display.loading}>
                                Start a backup
                            </Button>
                        </div>
                    </div>
                    <div className="col-md-2  d-flex align-items-end">
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


    /* Set the display property
 * @param propertyName name of the property
 * @param propertyValue the value
 */
    setDisplayProperty(propertyName, propertyValue) {
        let displayObject = this.state.display;
        displayObject[propertyName] = propertyValue;
        this.setState({display: displayObject});
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
            this.setState({error:{status: 500, error:httpPayload.getError()},
                    statusOperation: ""});
        } else {
            this.setState({statusOperation: httpPayload.getData().statusOperation})
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
            this.setState({statusOperation: "Error"});
        } else {
            this.setState(
                { resultCall:{
                        status: httpPayload.getData().status,
                        error:httpPayload.getData().error,
                        message:httpPayload.getData().message},
                    listBackup: httpPayload.getData().listBackup})
        }
    }


    startBackup() {
        let uri = '/blueberry/api/backup/start?';
        console.log("backup.startBackup http[" + uri + "]");
        var parameterBackup={ "nextId": ! this.state.parameter.explicit, "backupId": this.state.parameter.backupId}
        this.setDisplayProperty("loading", true);
        this.setState({status: ""});
        var restCallService = RestCallService.getInstance();
        restCallService.postJson(uri, parameterBackup, this, this.startBackupCallback);
    }

    startBackupCallback(httpPayload) {
        this.setDisplayProperty("loading", false);
        if (httpPayload.isError()) {
            debugger;
            console.log("Backup.startBackupCallback: error " + httpPayload.getError());
            this.setState({statusOperation: "Error"});
        } else {
            this.setState(
                { resultCall:{
                        status: httpPayload.getData().status,
                        error:httpPayload.getData().error,
                        message:httpPayload.getData().message,
                        component:httpPayload.getData().component
                    },
                    backup: httpPayload.getData().backupId,
                    statusOperation:httpPayload.getData().statusOperation});

        }
    }


}

export default Backup;