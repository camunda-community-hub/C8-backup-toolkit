// -----------------------------------------------------------
//
// Parameters
//
// List of all runners available
//
// -----------------------------------------------------------

import React from 'react';
import {Button, InlineNotification} from "carbon-components-react";
import {ArrowRepeat} from "react-bootstrap-icons";
import RestCallService from "../services/RestCallService";

class Parameters extends React.Component {


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
                        <h1 className="title">Parameters</h1>
                        <InlineNotification kind="info" hideCloseButton="true" lowContrast="false">
                            Current parameters of Blueberry
                        </InlineNotification>

                    </div>
                </div>
                <div className="row" style={{marginTop: "10px"}}>
                    <div className="col-md-2">
                        <Button className="btn btn-success btn-sm"
                                onClick={() => {
                                    this.refresh()
                                }}
                                disabled={this.state.display.loading}>
                            <ArrowRepeat/> Checkup
                        </Button>
                    </div>
                </div>

            </div>
        )
    }

    refresh() {
        let uri = '/blueberry/api/parameters/getall?';
        console.log("platform.checkup http[" + uri + "]");

        this.setDisplayProperty("loading", true);
        this.setState({status: ""});
        var restCallService = RestCallService.getInstance();
        restCallService.getJson(uri, this, this.refreshParametersCallback);
    }

    refreshParametersCallback(httpPayload) {
        this.setDisplayProperty("loading", false);
        if (httpPayload.isError()) {
            console.log("Configuration.startBackupCallback: error " + httpPayload.getError());
            this.setState({status: "Error"});
        } else {
            this.setState({paramters: httpPayload.getData()})
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

export default Parameters;