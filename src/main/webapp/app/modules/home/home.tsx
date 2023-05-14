import './home.scss';

import React from 'react';
import { Link } from 'react-router-dom';

import { Row, Col, Alert } from 'reactstrap';

import { useAppSelector } from 'app/config/store';

export const Home = () => {
  const account = useAppSelector(state => state.authentication.account);

  return (
    <Row>
      <Col md="3" className="pad">
        <span className="hipster rounded" />
      </Col>
      <Col md="9">
        <h2>Willkommen, bei EBAY!</h2>
        <p className="lead">Dies ist Ihre Hauptseite</p>
        {account?.login ? (
          <div>
            <Alert color="success">Sie sind als Benutzer &quot;{account.login}&quot; angemeldet.</Alert>
          </div>
        ) : (
          <div>
            <Alert color="warning">
              Wenn Sie sich
              <span>&nbsp;</span>
              <Link to="/login" className="alert-link">
                anmelden
              </Link>
              möchten, versuchen Sie es mit <br />- Administrator (Name=&quot;admin&quot; und Passwort=&quot;admin&quot;)
              <br />- Benutzer (Name=&quot;user&quot; und Passwort=&quot;user&quot;).
            </Alert>

            <Alert color="warning">
              Sie haben noch keinen Zugang?&nbsp;
              <Link to="/account/register" className="alert-link">
                Registrieren Sie sich
              </Link>
            </Alert>
          </div>
        )}
      </Col>
    </Row>
  );
};

export default Home;
