// -----------------------------------------------------------
//
// BlueberryApps
//
// Manage the main application
//
// -----------------------------------------------------------

import React from 'react';
import './index.scss';

import 'bootstrap/dist/css/bootstrap.min.css';

import {Container, Nav, Navbar} from 'react-bootstrap';
import Dashboard from "./dashboard/Dashboard";
import Scheduler from "./scheduler/Scheduler";
import Backup from "./backup/Backup"
import Restore from "./restore/Restore"
import Platform from "./platform/Platform"
import Configuration from "./configuration/Configuration";
import OperationLog from "./operationlog/OperationLog"
import HeaderMessage from "./HeaderMessage/HeaderMessage";

const FRAME_NAME = {
  DASHBOARD: "Dashboard",
  BACKUP: "Backup",
  SCHEDULER: "Scheduler",
  RESTORE: "Restore",
  CHECKUP: "Platform",
  CONFIGURATION: "Configuration"

}

class BlueberryApp extends React.Component {


  constructor(_props) {
    super();
    this.state = {frameContent: FRAME_NAME.DASHBOARD};
    this.clickMenu = this.clickMenu.bind(this);
  }


  render() {
    return (
      <div>

        <Navbar bg="light" variant="light">
          <Container>
            <Nav className="mr-auto">
              <Navbar.Brand href="#home">
                <img src="/img/blueberry.png" width="28" height="28" alt="Blueberry"/>
                Blueberry Backup
              </Navbar.Brand>

              <Nav.Link onClick={() => {
                this.clickMenu(FRAME_NAME.DASHBOARD)
              }}>Dashboard</Nav.Link>

              <Nav.Link onClick={() => {
                this.clickMenu(FRAME_NAME.BACKUP)
              }}>Backup</Nav.Link>

              <Nav.Link onClick={() => {
                this.clickMenu(FRAME_NAME.RESTORE)
              }}>Restore</Nav.Link>

              <Nav.Link onClick={() => {
                this.clickMenu(FRAME_NAME.SCHEDULER)
              }}>Scheduler</Nav.Link>


              <Nav.Link onClick={() => {
                this.clickMenu(FRAME_NAME.CHECKUP)
              }}>Checkup</Nav.Link>

              <Nav.Link onClick={() => {
                this.clickMenu(FRAME_NAME.CONFIGURATION)
              }}>Configuration</Nav.Link>

            </Nav>
          </Container>
        </Navbar>
        <HeaderMessage/>
        {this.state.frameContent === FRAME_NAME.DASHBOARD && <Dashboard/>}
        {this.state.frameContent === FRAME_NAME.BACKUP && <Backup/>}
        {this.state.frameContent === FRAME_NAME.SCHEDULER && <Scheduler/>}
        {this.state.frameContent === FRAME_NAME.RESTORE && <Restore/>}
        {this.state.frameContent === FRAME_NAME.CHECKUP && <Platform/>}
        {this.state.frameContent === FRAME_NAME.CONFIGURATION && <Configuration/>}


      </div>);
  }


  clickMenu(menu) {
    console.log("ClickMenu " + menu);
    this.setState({frameContent: menu});

  }

}

export default BlueberryApp;


