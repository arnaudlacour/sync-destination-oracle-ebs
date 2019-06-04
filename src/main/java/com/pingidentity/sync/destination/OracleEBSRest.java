package com.pingidentity.sync.destination;

import com.unboundid.directory.sdk.sync.api.SyncDestination;
import com.unboundid.directory.sdk.sync.config.SyncDestinationConfig;
import com.unboundid.directory.sdk.sync.types.EndpointException;
import com.unboundid.directory.sdk.sync.types.PostStepResult;
import com.unboundid.directory.sdk.sync.types.SyncOperation;
import com.unboundid.directory.sdk.sync.types.SyncServerContext;
import com.unboundid.ldap.sdk.*;
import com.unboundid.util.args.*;

import java.util.List;

public class OracleEBSRest extends SyncDestination {
    public static final String ARG_NAME_URL = "URL";
    public static final String ARG_NAME_ATTRIBUTE = "oracleEBSId";
    public static final String ARG_ATTRIBUTE_DEFAULT = "description";
    private String url;
    private String attributeType;

    @Override
    public String getExtensionName() {
        return "OracleEBSRest";
    }

    @Override
    public String[] getExtensionDescription() {
        return new String[]{"This extension handles Oracle EBS endpoints"};
    }

    /**
     * Performs the necessary processing to declare arguments for this extension
     *
     * @param parser
     * @throws ArgumentException
     */
    @Override
    public void defineConfigArguments(ArgumentParser parser) throws ArgumentException {
        StringArgument urlArgument = new StringArgument(null, ARG_NAME_URL, true, 1, "{URL}", "The URL to the Oracle EBS endpoint");
        urlArgument.addValueValidator(new URLArgumentValueValidator());
        parser.addArgument(urlArgument);

        StringArgument attributeArgument = new StringArgument(null, ARG_NAME_ATTRIBUTE, false, 1, "{attribute}", "the attribute type where a user ID will be written back to the source entry", ARG_ATTRIBUTE_DEFAULT);
        parser.addArgument(attributeArgument);
    }

    @Override
    public void initializeSyncDestination(SyncServerContext serverContext, SyncDestinationConfig config, ArgumentParser parser) throws EndpointException {
        url = parser.getStringArgument(ARG_NAME_URL).getValue();
        attributeType = parser.getStringArgument(ARG_NAME_ATTRIBUTE).getValue();
    }


    @Override
    public String getCurrentEndpointURL() {
        return url;
    }

    @Override
    public void createEntry(Entry entry, SyncOperation syncOperation) throws EndpointException {
        // call the API

        // this you obtain after a successful REST call to Oracle
        byte[] theUersOracleEBSId = null;

        LDAPInterface ldapi = (LDAPInterface) syncOperation.getAttachment("connection");

        try {
            Modification modification = new Modification(ModificationType.ADD, attributeType, theUersOracleEBSId);
            LDAPResult ldapResult = ldapi.modify(entry.getDN(), modification);
            if (!ResultCode.SUCCESS.equals(ldapResult.getResultCode())) {
                throw new EndpointException(PostStepResult.RETRY_OPERATION_LIMITED, "something went wrong with the LDAP callback" + ldapResult.getDiagnosticMessage());
            }
        } catch (LDAPException e) {
            throw new EndpointException(PostStepResult.ABORT_OPERATION);
        }
    }

    @Override
    public void modifyEntry(Entry entry, List<Modification> list, SyncOperation syncOperation) throws EndpointException {

    }

    @Override
    public void deleteEntry(Entry entry, SyncOperation syncOperation) throws EndpointException {
    }
}
